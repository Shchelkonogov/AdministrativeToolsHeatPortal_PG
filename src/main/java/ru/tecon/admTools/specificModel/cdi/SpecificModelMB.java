package ru.tecon.admTools.specificModel.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.specificModel.ejb.SpecificModelLocal;
import ru.tecon.admTools.specificModel.model.Condition;
import ru.tecon.admTools.specificModel.model.DataModel;
import ru.tecon.admTools.specificModel.model.GraphDecreaseItemModel;
import ru.tecon.admTools.specificModel.model.additionalModel.AnalogData;
import ru.tecon.admTools.specificModel.model.additionalModel.EnumerateData;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;
import ru.tecon.admTools.utils.TeconMessage;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Контроллер для формы конкретная модель
 */
@Named("specificModel")
@ViewScoped
public class SpecificModelMB implements Serializable {

    private boolean inIframe;

    // Параметр для переключения с конкретной модели на конкретную модель экомониторинга
    private boolean eco = false;

    private int objectID;
    private String objectPath;

    // Модели данных для закладки аналоговые параметры
    private List<DataModel> tableModel;
    private List<DataModel> filteredTableModel = new ArrayList<>();
    private DataModel selectedData;
    private Set<String> techProcFilter;
    private Set<String> paramNameFilter;

    // Модели данных для всплывающих окон на закладке аналоговые параметры

    // Данные для изменения
    private DataModel changedDecreaseGraphItem;
    private int rowIndex;

    // Модели данных для всплывающего окна выбора суточного снижения
    private List<GraphDecreaseItemModel> decreases;
    private GraphDecreaseItemModel selectDecrease;

    // Модель данных для всплывающего окна выбора графика или оптимального значения
    private List<GraphDecreaseItemModel> graphs;
    private GraphDecreaseItemModel selectGraph;
    private String optValue = "";

    // Модели данных для закладки перечислимые параметры

    // Левая таблица с основными данными
    private List<DataModel> enumerableTableModel;
    private DataModel selectedEnumerateItem;

    // Правая таблица с дополнительными данными
    private EnumerateData paramConditionDataModel = new EnumerateData();
    private List<Condition> conditions;

    @EJB
    private SpecificModelLocal bean;

    @Inject
    private SystemParamsUtilMB utilMB;
    @Inject
    private transient Logger logger;

    @PostConstruct
    private void init() {
        // По факту это заглушка для запуска инициализации контроллера SystemParamsUtilMB
        logger.log(Level.INFO, "Init data {0}", utilMB);

        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        objectID = Integer.parseInt(request.get("objectId"));

        eco = Boolean.parseBoolean(request.get("eco"));

        objectPath = bean.getObjectPath(objectID);

        PrimeFaces.current().executeScript("load();");

        if (!eco) {
            conditions = bean.getConditions();
        }
        decreases = bean.getDecreases();
        graphs = bean.getGraphs();
    }

    /**
     * Метод для загрузки данных
     */
    public void loadData() {
        tableModel = bean.getData(objectID, eco);
        //проводим лексический разбор для установления какое диалоговое окно будет открываться
        for (DataModel dataModel: tableModel) {
            if (dataModel.getParamTypeName().equals("Температура")) {
                if (dataModel.getTechProcCode().equals("ГВС")) {
                    dataModel.setDecreaseValueRender(true);
                } else {
                    dataModel.setTempGraphRender(true);
                }
            }
            if (dataModel.getParamTypeName().equals("Давление")) {
                dataModel.setOptValuesRender(true);
            }
            //объединяем информацию из разных столбцов выгруженной таблицы в один на форме График/Опт.знач/Суточные снижения
            if (!((AnalogData) dataModel.getAdditionalData()).isDecreaseDisable()) {
                ((AnalogData) dataModel.getAdditionalData()).setGraph(((AnalogData) dataModel.getAdditionalData()).getGraphID(),
                        ((AnalogData) dataModel.getAdditionalData()).getGraphName(), false);
            }
            if (((AnalogData) dataModel.getAdditionalData()).getGraphName() == null &&
                    ((AnalogData) dataModel.getAdditionalData()).getDecreaseName() != null) {
                ((AnalogData) dataModel.getAdditionalData()).setGraph(((AnalogData) dataModel.getAdditionalData()).getGraphID(),
                        ((AnalogData) dataModel.getAdditionalData()).getDecreaseName(),
                        ((AnalogData) dataModel.getAdditionalData()).isGraphDisable());
            }
        }

        filteredTableModel.clear();
        filteredTableModel.addAll(tableModel);

        if (!eco) {
            enumerableTableModel = bean.getEnumerableData(objectID);

            if (selectedEnumerateItem != null) {
                selectedEnumerateItem = enumerableTableModel.stream()
                        .filter(dataModel -> dataModel.getId().equals(selectedEnumerateItem.getId()))
                        .findFirst().orElse(null);
                if (selectedEnumerateItem != null) {
                    selectedEnumerateItem.setAdditionalData(bean.getParamCondition(objectID, selectedEnumerateItem.getParID(), selectedEnumerateItem.getStatAgr()));
                    paramConditionDataModel = (EnumerateData) selectedEnumerateItem.getAdditionalData();
                }
            }
        } else {
            enumerableTableModel = new ArrayList<>();
        }

        techProcFilter = tableModel.stream().map(DataModel::getTechProcCode).collect(Collectors.toSet());
        paramNameFilter = tableModel.stream().map(DataModel::getParamTypeName).collect(Collectors.toSet());
    }

    /**
     * Метод обрабатывает инициализацию события изменения значения ячейки в столбце График/Опт.знач/Суточные снижения
     * в таблице аналоговых параметров
     *
     * @param event событие
     */
    public void onCellEditInit(CellEditEvent<?> event) {
        rowIndex = event.getRowIndex();
        changedDecreaseGraphItem = filteredTableModel.get(rowIndex);

        if (event.getColumn().getClientId().endsWith("graphColumn")) {
            if ((((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getGraphID() == null) &&
                    (((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getGraphName() != null)) {
                optValue = ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getGraphName();
            } else {
                optValue = "";
            }
            if (changedDecreaseGraphItem.isTempGraphRender()) {
                PrimeFaces.current().executeScript("PF('graphDialog').show();");
            }
            if (changedDecreaseGraphItem.isOptValuesRender()) {
                PrimeFaces.current().ajax().update("optValueSelectForm");
                PrimeFaces.current().executeScript("PF('optValueDialog').show();");
            }
            if (changedDecreaseGraphItem.isDecreaseValueRender()) {
                PrimeFaces.current().executeScript("PF('decreaseDialog').show();");
            }
        }
    }

    /**
     * Метод обрабатывает событие изменения значения ячейки таблицы аналоговых параметров
     *
     * @param event событие
     */
    public void onCellEdit(CellEditEvent<?> event) {
        filteredTableModel.get(event.getRowIndex()).setChange(true);
    }

    /**
     * Метод обрабатывает нажатие на кнопку сохранить и сохраняет все изменения в базе
     */
    public void saveData() {
        //логируем все изменения
        logger.info("update analog objects:");
        tableModel.stream().filter(DataModel::isChange).forEach(dataModel -> logger.info(dataModel.toString()));
        logger.info("end;");

        logger.info("update enumerate objects:");
        enumerableTableModel.stream().filter(DataModel::isChange).forEach(dataModel -> logger.info(dataModel.toString()));
        logger.info("end;");

        //цикл для сохранения всех изменений в таблице аналоговых параметров
        tableModel.stream().filter(DataModel::isChange).forEach(dataModel -> {

            logger.info("save analog data: " + dataModel);

            try {
                bean.saveAParam(objectID, dataModel, utilMB.getLogin(), eco);
            } catch (SystemParamException e) {
                if (inIframe) {
                    new TeconMessage(TeconMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()).send();
                } else {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()));
                }
            }
        });

        //цикл для сохранения всех изменений в таблице перечислимых параметров
        enumerableTableModel.stream().filter(DataModel::isChange).forEach(dataModel -> {
            logger.info("save enum data: " + dataModel);
            try {
                bean.saveEnumParam(objectID, dataModel, utilMB.getLogin());
            } catch (SystemParamException e) {
                if (inIframe) {
                    new TeconMessage(TeconMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()).send();
                } else {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()));
                }
            }
        });

        PrimeFaces.current().executeScript("load();");
    }

    /**
     * Метод обрабатывает нажатие contextMenu Сбросить границы
     */
    public void clearRanges(int objectID, int parID, int statAgrID) throws SystemParamException {
        bean.clearRanges(objectID, parID, statAgrID, eco, utilMB.getLogin());
    }

    /**
     * Метод обрабатывает нажатие на checkBox в таблице с аналоговыми параметрами
     *
     * @param rowIndex индекс строки
     */
    public void onBooleanValueChange(int rowIndex) {
        filteredTableModel.get(rowIndex).setChange(true);
    }

    /**
     * Метод обрабатывает нажатие на выбрать во всплывающем окне выбора суточного снижения
     */
    public void saveDecrease() {
        ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setDecreaseName(selectDecrease.getName());
        ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setDecreaseID(selectDecrease.getId());
        ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphName(selectDecrease.getName());
        changedDecreaseGraphItem.setChange(true);
        PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
    }

    /**
     * Метод обрабатывает нажатие на Без снижения во всплывающем окне выбора суточного снижения
     */
    public void saveNoDecrease() {
        if (((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getDecreaseName() != null) {
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setDecreaseName(null);
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setDecreaseID(null);
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphName(null);
            changedDecreaseGraphItem.setChange(true);
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
        }
    }

    /**
     * Метод обрабатывает нажатие на выбрать во всплывающем окне выбора графика
     */
    public void saveGraph() {
        ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphName(selectGraph.getName());
        ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphID(selectGraph.getId());
        changedDecreaseGraphItem.setChange(true);
        PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
    }

    /**
     * Метод обрабатывает нажатие на Без графика во всплывающем окне выбора графика
     */
    public void saveNoGraph() {
        if (((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getGraphID() != null) {
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphName("");
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphID(null);
            changedDecreaseGraphItem.setChange(true);
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
        }
    }

    /**
     * Метод обрабатывает событие выделения строки в таблице с перечислимыми параметрами
     */
    public void onRowSelect() {
        if (selectedEnumerateItem.getAdditionalData() == null) {
            paramConditionDataModel = bean.getParamCondition(objectID, selectedEnumerateItem.getParID(), selectedEnumerateItem.getStatAgr());
            selectedEnumerateItem.setAdditionalData(paramConditionDataModel);
        } else {
            paramConditionDataModel = (EnumerateData) selectedEnumerateItem.getAdditionalData();
        }
    }

    /**
     * Метод обрабатывает событие изменения состояний в уточняющей таблице перечислимых параметров
     *
     * @param event событие
     */
    public void onCellEnumEdit(CellEditEvent<?> event) {
        ((EnumerateData) selectedEnumerateItem.getAdditionalData()).getConditions().get(event.getRowIndex()).setEdited(true);
        selectedEnumerateItem.setChange(true);
    }

    /**
     * Метод обрабатывает нажание кнопки выбрать в диалоговом окне выбора оптимального значения
     */
    public void saveOptValue() {
        AnalogData data = (AnalogData) changedDecreaseGraphItem.getAdditionalData();
        List<String> updateList = new ArrayList<>();

        if ((data.getGraphID() != null) || (data.getGraphName() == null) || (data.getGraphName().equals(""))) {
            data.setA(-10d, 10d, false, false, true);
            data.setT(-5d, 5d, false, false, true);
            data.setAbsolute(true);

            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":tMinCellEditor");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":tMaxCellEditor");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":tPercentColumn");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":aMinCellEditor");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":aMaxCellEditor");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":aPercentColumn");
            updateList.add("tabView:tableForm:tableData:" + rowIndex + ":absoluteColumn");
        }

        data.setGraphName(optValue);
        data.setGraphID(null);

        updateList.add("tabView:tableForm:tableData:" + rowIndex + ":graphCellEditor");

        changedDecreaseGraphItem.setChange(true);
        PrimeFaces.current().ajax().update(updateList);
    }

    /**
     * Метод обрабатывает нажание кнопки Без Опт.знач в диалоговом окне выбора оптимального значения
     */
    public void saveNoOptValue() {
        if (((AnalogData) changedDecreaseGraphItem.getAdditionalData()).getGraphName() != null) {
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphName("");
            ((AnalogData) changedDecreaseGraphItem.getAdditionalData()).setGraphID(null);
            changedDecreaseGraphItem.setChange(true);
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":graphValue");
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":absoluteColumn");
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":aPercentColumn");
            PrimeFaces.current().ajax().update("tabView:tableForm:tableData:" + rowIndex + ":tPercentColumn");
        }
    }

    /**
     * Расчет значения границы с отклонением в переданное значение процентов
     *
     * @param defaultPercentValue отклонение в процентах
     * @return рассчитанное значение границы
     */
    public String getBorder(int defaultPercentValue) {
        try {
            return new BigDecimal(optValue)
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(defaultPercentValue))
                    .add(new BigDecimal(optValue))
                    .setScale(2, RoundingMode.HALF_UP).toString();
        } catch (NumberFormatException e) {
            return defaultPercentValue + "%";
        }
    }

    public void setOptValue(String optValue) {
        this.optValue = optValue;
    }

    public String getOptValue() {
        return optValue;
    }

    public String getObjectPath() {
        return objectPath;
    }

    public List<DataModel> getTableModel() {
        return tableModel;
    }

    public List<DataModel> getFilteredTableModel() {
        return filteredTableModel;
    }

    public void setFilteredTableModel(List<DataModel> filteredTableModel) {
        this.filteredTableModel = filteredTableModel;
    }

    public Set<String> getTechProcFilter() {
        return techProcFilter;
    }

    public List<GraphDecreaseItemModel> getDecreases() {
        return decreases;
    }

    public GraphDecreaseItemModel getSelectDecrease() {
        return selectDecrease;
    }

    public void setSelectDecrease(GraphDecreaseItemModel selectDecrease) {
        this.selectDecrease = selectDecrease;
    }

    public List<GraphDecreaseItemModel> getGraphs() {
        return graphs;
    }

    public GraphDecreaseItemModel getSelectGraph() {
        return selectGraph;
    }

    public void setSelectGraph(GraphDecreaseItemModel selectGraph) {
        this.selectGraph = selectGraph;
    }

    public List<DataModel> getEnumerableTableModel() {
        return enumerableTableModel;
    }

    public DataModel getSelectedEnumerateItem() {
        return selectedEnumerateItem;
    }

    public void setSelectedEnumerateItem(DataModel selectedEnumerateItem) {
        this.selectedEnumerateItem = selectedEnumerateItem;
    }

    public EnumerateData getParamConditionDataModel() {
        return paramConditionDataModel;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public Set<String> getParamNameFilter() {
        return paramNameFilter;
    }

    public int getObjectID() {
        return objectID;
    }

    public boolean isEco() {
        return eco;
    }

    public DataModel getSelectedData() {
        return selectedData;
    }

    public void setSelectedData(DataModel selectedData) {
        this.selectedData = selectedData;
    }

    public void changeInIframe() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        inIframe = Boolean.parseBoolean(params.get("inIframe"));
    }
}
