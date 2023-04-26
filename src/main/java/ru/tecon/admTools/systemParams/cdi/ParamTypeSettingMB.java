package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MeasureSB;
import ru.tecon.admTools.systemParams.ejb.ParamTypeSettingSB;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Maksim Shchelkonogov
 * 16.02.2023
 */
@Named("paramTypeSetting")
@ViewScoped
public class ParamTypeSettingMB implements Serializable {

    private List<ParamTypeTable> types = new ArrayList<>();
    private List<ParamTypeTable> typesFilter = new ArrayList<>();
    private ParamTypeTable selectedType;
    private final List<ParamTypeTable> newTypes = new ArrayList<>();
    private ParamType newParamType;

    private List<ParamPropTableA> propTableA = new ArrayList<>();
    private ParamPropTableA selectedPropA;
    private ParamPropTableA newPropA = new ParamPropTableA();
    private List<ParamPropTableP> propTableP = new ArrayList<>();
    private ParamPropTableP selectedPropP;
    private ParamPropTableP newPropP = new ParamPropTableP();

    private List<Properties> paramProperties = new ArrayList<>();

    private List<Condition> paramConditions = new ArrayList<>();

    private boolean disableTypeRemoveBtn = true;
    private boolean disablePropRemoveBtn = true;
    private boolean disablePropAddBtn = true;

    private List<Measure> measures;
    private List<StatisticalAggregate> statAggregates;

    boolean init = false;

    @Inject
    private transient Logger logger;

    @Inject
    private SystemParamsUtilMB utilMB;

    @EJB
    private ParamTypeSettingSB bean;

    @EJB
    private MeasureSB measureBean;

    @PostConstruct
    private void init() {
        loadData();

        init = true;
    }

    /**
     * Первоначальная загрузка
     */
    public void loadData() {
        if (init) {
            init = false;
            return;
        }

        logger.info("update data for form paramTypeSetting");
        typesFilter = types = bean.getParamTypes();

        newTypes.clear();
        newParamType = null;

        paramConditions = bean.getParamConditions();
        measures = measureBean.getMeasures();

        selectedType = null;

        propTableA.clear();
        propTableP.clear();

        selectedPropA = null;
        selectedPropP = null;

        newPropA = new ParamPropTableA();
        newPropP = new ParamPropTableP();

        disableTypeRemoveBtn = true;
        disablePropRemoveBtn = true;
        disablePropAddBtn = true;
    }

    /**
     * Обработчик нажатия кнопки Добавить свойство в всплывающем окне добавить новый тип параметра
     */
    public void onAddNewRow() {
        newTypes.add(new ParamTypeTable(new StatisticalAggregate("Новый агрегат")));
    }

    /**
     * @return список для сортировки таблицы по типу параметра
     */
    public List<String> getTypeFilterValues() {
        return types.stream().map(paramTypeTable -> paramTypeTable.getParamType().getName()).distinct().collect(Collectors.toList());
    }

    /**
     * Обработчик выбора checkBox в колонке визуализация
     * @param data строка в которой нажали кнопку
     * @param rowIndex индекс строки
     */
    public void onChange(ParamTypeTable data, int rowIndex) {
        try {
            bean.changeGenStat(data, utilMB.getLogin(), utilMB.getIp());

            int paramTypeID = data.getParamType().getId();
            List<String> updateItems = new ArrayList<>();
            updateItems.add("typeSettingForm:typeTable:" + rowIndex + ":mainAggregate");

            for (int i = 0; i < typesFilter.size(); i++) {
                ParamTypeTable filterItem = typesFilter.get(i);
                if ((filterItem.getParamType().getId() == paramTypeID) && filterItem.isMainAggregate() && (i != rowIndex)) {
                    filterItem.setMainAggregate(false);
                    updateItems.add("typeSettingForm:typeTable:" + i + ":mainAggregate");
                }
            }

            PrimeFaces.current().ajax().update(updateItems);
        } catch (SystemParamException e) {
            data.setMainAggregate(false);
            PrimeFaces.current().ajax().update(Arrays.asList("typeSettingForm:typeTable:" + rowIndex + ":mainAggregate", "growl"));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", e.getMessage()));
        }
    }

    /**
     * Обработик выбора типа параметра. Загружает данные для свойств.
     * @param event событие выделения строки
     */
    public void onTypeSelect(SelectEvent<ParamTypeTable> event) {
        ParamTypeTable eventObject = event.getObject();

        if (eventObject.getCategory() != null) {
            try {
                switch (eventObject.getCategory()) {
                    case "A": {
                        propTableA = bean.getTypePropsA(eventObject);
                        break;
                    }
                    case "P": {
                        propTableP = bean.getTypePropsP(eventObject);
                        break;
                    }
                }

                disableTypeRemoveBtn = false;
                disablePropRemoveBtn = true;
                disablePropAddBtn = false;
            } catch (SystemParamException e) {
                propTableP.clear();
                propTableA.clear();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка поиска", e.getMessage()));
            }
        } else {
            disableTypeRemoveBtn = false;
            disablePropRemoveBtn = true;
            disablePropAddBtn = true;
        }
    }

    /**
     * @return заголовок таблицы свойств
     */
    public String getPropHeader() {
        return "Свойства" +
                ((selectedType != null) && (selectedType.getAggregate() != null) ?
                        " типа параметра \"" + selectedType.getParamType().getName() + "\" агрегата \"" + selectedType.getAggregate().getName() + "\"" :
                        "");
    }

    /**
     * Обработчик удаления типа параметра (нажатие на -). Удаляет выделенный агрегат типа параметра,
     * если тип без агрегата, то удаляет тип
     */
    public void onRemoveTypeAggregate() {
        logger.log(Level.INFO, "remove type aggregate {0}", selectedType);

        try {
            if (selectedType.getAggregate() == null) {
                bean.removeType(selectedType, utilMB.getLogin(), utilMB.getIp());
            } else {
                bean.removeTypeAggregate(selectedType, utilMB.getLogin(), utilMB.getIp());
            }

            loadData();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик удаления типа параметра (нажатия в контекстном меню)
     */
    public void removeType() {
        logger.log(Level.INFO, "remove type {0}", selectedType);

        try {
            bean.removeType(selectedType, utilMB.getLogin(), utilMB.getIp());

            loadData();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    public void onCreateTypeWrapper() {
        PrimeFaces.current().executeScript("saveTypeWrapper()");
    }

    /**
     * При открытии всплывающего окна добавить новый тип в списке выбирается тип (к которому добавлять агрегаты),
     * если его выделили заранее в таблице
     */
    public void initAddType() {
        if (selectedType != null) {
            PrimeFaces.current().executeScript("PF('selectAddTypeVW').selectValue(" + selectedType.getParamType().getId() + ");");
        }
    }

    /**
     * Обработчик кнопки сохранить в окне дабавить новый тип параметра.
     * Создает тип, если его нет и добавляет все агрегаты
     */
    public void onCreateType() {
        logger.log(Level.INFO, "create new type {0} {1}", new Object[] {newParamType, newTypes});

        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (newParamType.isCreate()) {
                bean.addType(newParamType, utilMB.getLogin(), utilMB.getIp());
            }

            if (!newTypes.isEmpty()) {
                newTypes.removeIf(ParamTypeTable::check);

                for (ParamTypeTable newTypeElement: newTypes) {
                    newTypeElement.setParamType(newParamType);

                    try {
                        bean.addAggregateToType(newTypeElement, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
                    }
                }

                newTypes.forEach(System.out::println);
            }

            loadData();

            PrimeFaces.current().executeScript("PF('typeTableWV').filter(); PF('addTypeDialog').hide();");
        } catch (SystemParamException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * При выборе типа параметра в выпадающем меню акна добавить новы тип параметра загружает список доступных агрегатов
     */
    public void loadPropertiesForNewType() {
        statAggregates = bean.getListStatAggregateForParamType(newParamType.getId());
        newTypes.clear();
    }

    /**
     * Обработчик закрытия окна добавить новый тип
     */
    public void onAddTypeDialogClose() {
        newTypes.clear();
        newParamType = null;
    }

    public void onPropSelect(SelectEvent event) {
        logger.log(Level.INFO, "select prop {0}", event.getObject());

        disablePropRemoveBtn = false;
    }

    public void onAddPropDialogAClose() {
        newPropA = new ParamPropTableA();
    }

    public void onAddPropDialogPClose() {
        newPropP = new ParamPropTableP();
    }

    /**
     * Обработчик нажатия + на таблице свойств параметра.
     * Открывает соответствующее окно в зависимости от категории (аналоговый / перечислимый)
     * @param type категория параметра
     */
    public void loadProperties(String type) {
        switch (type) {
            case "A": {
                paramProperties = bean.getParamProperties(selectedType);

                PrimeFaces.current().ajax().update("addPropDialogA");
                PrimeFaces.current().executeScript("PF('addPropDialogA').show();");
                break;
            }
            case "P": {
                PrimeFaces.current().ajax().update("addPropDialogP");
                PrimeFaces.current().executeScript("PF('addPropDialogP').show();");
                break;
            }
        }
    }

    public void onCreatePropWrapper() {
        PrimeFaces.current().executeScript("savePropWrapper()");
    }

    /**
     * Обработчик создания нового свойства типа параметра в окне добавить новое свойство
     * @param type категория параметра
     */
    public void onCreateProp(String type) {
        try {
            switch (type) {
                case "A": {
                    logger.log(Level.INFO, "create new property {0} for type {1}", new Object[]{newPropA, selectedType});

                    bean.addPropA(selectedType, newPropA, utilMB.getLogin(), utilMB.getIp());

                    propTableA = bean.getTypePropsA(selectedType);
                    selectedPropA = null;

                    PrimeFaces.current().executeScript("PF('addPropDialogA').hide();");
                    break;
                }
                case "P": {
                    logger.log(Level.INFO, "create new property {0} for type {1}", new Object[]{newPropP, selectedType});

                    bean.addPropP(selectedType, newPropP, utilMB.getLogin(), utilMB.getIp());

                    propTableP = bean.getTypePropsP(selectedType);
                    selectedPropP = null;

                    PrimeFaces.current().executeScript("PF('addPropDialogP').hide();");
                    break;
                }
            }
            disablePropRemoveBtn = true;
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработчик нажатия - на таблице свойства параметра. Удаляет выбранное свойство
     * @param type категория параметра
     */
    public void onRemoveProp(String type) {
        try {
            switch (type) {
                case "A": {
                    logger.log(Level.INFO, "remove prop {0} for type {1}", new Object[]{selectedPropA, selectedType});

                    bean.removePropA(selectedType, selectedPropA, utilMB.getLogin(), utilMB.getIp());

                    propTableA = bean.getTypePropsA(selectedType);
                    selectedPropA = null;
                    break;
                }
                case "P": {
                    logger.log(Level.INFO, "remove prop {0} for type {1}", new Object[]{selectedPropP, selectedType});

                    bean.removePropP(selectedType, selectedPropP, utilMB.getLogin(), utilMB.getIp());

                    propTableP = bean.getTypePropsP(selectedType);
                    selectedPropP = null;
                    break;
                }
            }

            disablePropRemoveBtn = true;
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    public boolean isDisablePropAddBtn() {
        return disablePropAddBtn;
    }

    public ParamPropTableA getSelectedPropA() {
        return selectedPropA;
    }

    public void setSelectedPropA(ParamPropTableA selectedPropA) {
        this.selectedPropA = selectedPropA;
    }

    public ParamPropTableP getSelectedPropP() {
        return selectedPropP;
    }

    public void setSelectedPropP(ParamPropTableP selectedPropP) {
        this.selectedPropP = selectedPropP;
    }

    public ParamPropTableA getNewPropA() {
        return newPropA;
    }

    public void setNewPropA(ParamPropTableA newPropA) {
        this.newPropA = newPropA;
    }

    public ParamPropTableP getNewPropP() {
        return newPropP;
    }

    public void setNewPropP(ParamPropTableP newPropP) {
        this.newPropP = newPropP;
    }

    public List<Condition> getParamConditions() {
        return paramConditions;
    }

    public List<Properties> getParamProperties() {
        return paramProperties;
    }

    public List<ParamTypeTable> getNewTypes() {
        return newTypes;
    }

    public ParamType getNewParamType() {
        return newParamType;
    }

    public void setNewParamType(ParamType newParamType) {
        this.newParamType = newParamType;
    }

    public List<ParamType> getTypesList() {
        return types.stream().map(ParamTypeTable::getParamType).distinct().sorted().collect(Collectors.toList());
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public List<StatisticalAggregate> getStatAggregates() {
        return statAggregates;
    }

    public List<ParamTypeTable> getTypes() {
        return types;
    }

    public void setTypes(List<ParamTypeTable> types) {
        this.types = types;
    }

    public List<ParamTypeTable> getTypesFilter() {
        return typesFilter;
    }

    public void setTypesFilter(List<ParamTypeTable> typesFilter) {
        this.typesFilter = typesFilter;
    }

    public ParamTypeTable getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(ParamTypeTable selectedType) {
        this.selectedType = selectedType;
    }

    public boolean isDisableTypeRemoveBtn() {
        return disableTypeRemoveBtn;
    }

    public boolean isDisablePropRemoveBtn() {
        return disablePropRemoveBtn;
    }

    public List<ParamPropTableA> getPropTableA() {
        return propTableA;
    }

    public List<ParamPropTableP> getPropTableP() {
        return propTableP;
    }
}
