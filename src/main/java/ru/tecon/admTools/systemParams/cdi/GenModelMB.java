package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.GenModelSB;
import ru.tecon.admTools.systemParams.ejb.temperature.TemperatureLocal;
import ru.tecon.admTools.systemParams.model.genModel.*;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureStatus;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Контроллер для формы обобщенная модель
 *
 * @author Aleksey Sergeev
 */
@Named("genModelMB")
@ViewScoped
public class GenModelMB implements Serializable, AutoUpdate {

    // Поля описывающие данные для основной таблицы обобщенной модели
    private TreeNode<GMTree> root;
    private TreeNode<GMTree> rootFiltered;
    private TreeNode<GMTree> selectedObjNode;

    // Disable для кнопок удалить/добавить для основной таблицы обобщенной модели
    private boolean disableRemoveBtn = true;
    private boolean disableAddBtn = true;

    // Поле описывающие данные для графиков и суточных снижений
    private List<Temperature> graphOrDecreaseList;
    private TemperatureStatus temperatureStatus;

    // Текст заголовка таблицы свойств
    private String tableHeader;

    // Данные для описания параметра
    private Param paramList;
    private Param addParam = new Param();
    private boolean renderParamTable;

    // Данные для аналоговых свойств агрегата

    private List<ParamProp> paramPropList;
    private boolean renderParamPropTable;

    // Данные для перечислимых свойств агрегата

    private List<ParamPropPer> paramPropPerList;
    private boolean renderParamPropPerTable;

    // Данные для таблицы с формулой

    private ParamPropCalc propCalc = new ParamPropCalc();
    private ParamPropCalc newPropCalc = new ParamPropCalc();
    private boolean renderCalcAgrVars;

    @Inject
    private transient Logger logger;

    @EJB
    private GenModelSB genModelSB;

    @EJB(beanName = "tempGraphSB")
    private TemperatureLocal tempGraphBean;

    @EJB(beanName = "dailyReductionSB")
    private TemperatureLocal dailyReductionBean;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        root = rootFiltered = new DefaultTreeNode<>(new GMTree(), null);

        loadData();

        PrimeFaces.current().executeScript("PF('buiGenModelTable').show();");
        PrimeFaces.current().executeScript("PF('genModelWidget').clearFilters();");
        PrimeFaces.current().executeScript("PF('genModelWidget').unselectAllNodes()");
    }

    /**
     * Первоначальная загрузка данных для контроллера
     */
    private void loadData() {
        List<GMTree> expandedList = getRowDown(rootFiltered);

        root.getChildren().clear();

        Map<String, TreeNode<GMTree>> nodes = new HashMap<>();
        nodes.put(null, root);
        for (GMTree genTree: genModelSB.getTreeParam()) {
            TreeNode<GMTree> parent = nodes.get(genTree.getParent());
            DefaultTreeNode<GMTree> treeNode = new DefaultTreeNode<>(genTree, parent);

            if (expandedList.contains(genTree)) {
                treeNode.setExpanded(true);
            }

            nodes.put(genTree.getId(), treeNode);
        }

        tableHeader = "Свойства";
        tablesHide();
    }

    /**
     * Метод обрабатывает выбор строки в дереве обобщенной модели, для получения данных в таблицы
     *
     * @param event событие выбора строки
     */
    public void onRowSelect(NodeSelectEvent event) {
        GMTree selectedRow = (GMTree) event.getTreeNode().getData();

        logger.log(Level.INFO, "Selected row {0}", selectedRow);

        // Заблокировали кнопки удалить и добавить в основной таблице Обобщенной модели
        disableRemoveBtn = true;
        disableAddBtn = true;

        tablesHide();

        switch (selectedRow.getMyType()) {
            case "PP":
                tableHeader = "Параметр \"" + selectedRow.getName() + "\"";
                paramList = genModelSB.getParam(selectedRow.getMyId());

                // Проверка отображать ли колонки "графики"/"суточные снижения"/"оптимальное значение"
                switch (selectedObjNode.getParent().getData().getName()) {
                    case "Давление":
                        temperatureStatus = TemperatureStatus.OPT_VALUE;
                        break;
                    case "Температура":
                        if (selectedObjNode.getParent().getParent().getData().getName().equals("Горячее водоснабжение")) {
                            temperatureStatus = TemperatureStatus.DAILY_REDUCTION;
                        } else {
                            temperatureStatus = TemperatureStatus.GRAPH;
                        }
                        break;
                    default:
                        temperatureStatus = TemperatureStatus.EMPTY;
                }

                renderParamTable = true;
                disableRemoveBtn = false;
                break;
            case "SA":
                GMTree parentOfSelectedRow = (GMTree) event.getTreeNode().getParent().getData();
                tableHeader = "Свойства агрегата \"" + selectedRow.getName() + "\"";

                switch (selectedRow.getCateg()) {
                    case "A":
                        paramPropList = genModelSB.getParamProp(parentOfSelectedRow.getMyId(), selectedRow.getMyId());
                        renderParamPropTable = true;
                        break;
                    case "P":
                        paramPropPerList = genModelSB.getParamPropPer(parentOfSelectedRow.getMyId(), selectedRow.getMyId());
                        renderParamPropPerTable = true;
                        break;
                    case "F":
                        paramPropList = genModelSB.getParamProp(parentOfSelectedRow.getMyId(), selectedRow.getMyId());
                        propCalc = genModelSB.getParamPropCalc(parentOfSelectedRow.getMyId(), selectedRow.getMyId());
                        renderCalcAgrVars = true;
                        break;
                }
                break;
            case "PT":
                disableAddBtn = false;
            case "TP":
            default:
                tableHeader = "Свойства";
        }
    }

    /**
     * Настройка диалогового окна "Добавление нового параметра"
     */
    public void addParamDialogSetting() {
        switch (selectedObjNode.getData().getName()) {
            case "Давление":
                temperatureStatus = TemperatureStatus.OPT_VALUE;
                break;
            case "Температура":
                if (selectedObjNode.getParent().getData().getName().equals("Горячее водоснабжение")) {
                    temperatureStatus = TemperatureStatus.DAILY_REDUCTION;
                    graphOrDecreaseList = dailyReductionBean.getTemperatures();
                } else {
                    temperatureStatus = TemperatureStatus.GRAPH;
                    graphOrDecreaseList = tempGraphBean.getTemperatures();
                }
                break;
            default:
                temperatureStatus = TemperatureStatus.EMPTY;
        }
    }

    /**
     * Метод скрывает все таблицы
     */
    public void tablesHide() {
        renderParamTable = false;
        renderParamPropTable = false;
        renderParamPropPerTable = false;
        renderCalcAgrVars = false;
    }

    /**
     * Обработчик сохранения добавления нового параметра в дерево, нажатие на копку сохранить
     */
    public void onSaveChanges() {
        logger.log(Level.INFO, "Create new parameter {0}", addParam);

        try {
            genModelSB.addParam(selectedObjNode.getParent().getData().getMyId(), selectedObjNode.getData().getMyId(),
                    addParam, utilMB.getLogin(), utilMB.getIp());

            loadData();

            PrimeFaces.current().executeScript("PF('addNewParam').hide(); PF('buiGenModelTable').show(); PF('genModelWidget').filter();");
            PrimeFaces.current().ajax().update("genModelForm", "genModelTableForm:propPanelMain");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
    }

    /**
     * Функция составляет список ранее раскрытых объектов в дереве
     *
     * @return - возвращает список открытых объектов
     */
    private List<GMTree> getRowDown(TreeNode<GMTree> treeNode) {
        List<GMTree> expandedList = new ArrayList<>();

        for (int i = 0; i < treeNode.getChildCount(); i++) {
            if (!treeNode.getChildren().get(i).isLeaf()) {
                if (treeNode.getChildren().get(i).isExpanded()) {
                    expandedList.add(treeNode.getChildren().get(i).getData());
                }
                expandedList.addAll(getRowDown(treeNode.getChildren().get(i)));
            }
        }
        return expandedList;
    }

    /**
     * Метод обертка для разнесения вызова диалогового окна для сохранения значения в базу при сохранении нового параметра
     */
    public void saveParamWrapper() {
        PrimeFaces.current().executeScript("saveParamWrapper()");
    }

    /**
     * Обработчик обновления диалогового окна в случае его закрытия
     */
    public void onAddParamDialogClose() {
        addParam = new Param();
    }

    /**
     * Обработчик удаления параметра из дерева, нажатие на копку -
     */
    public void onRemoveParam() {
        logger.log(Level.INFO, "Remove param: {0}", selectedObjNode);

        try {
            genModelSB.removeParam(selectedObjNode.getData().getMyId(), utilMB.getLogin(), utilMB.getIp());

            loadData();
            disableRemoveBtn = true;
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик нажатия на галку в столбце визуализации
     */
    public void changeVisualization(DefaultTreeNode<GMTree> item) {
        logger.log(Level.INFO, "change visualization for {0}", item);

        long parentId = ((GMTree) item.getParent().getData()).getMyId();

        try {
            genModelSB.updParamStat(parentId, item.getData(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка изменения визуализации", e.getMessage()));
            item.getData().setVisStat(!item.getData().isVisStat());
        }
    }

    /**
     * Обработчик сохранения изменения строки для таблицы параметров
     *
     * @param event событие
     */
    public void onRowEditParam(RowEditEvent<Param> event) {
        logger.log(Level.INFO, "update row {0}", event.getObject());
        try {
            genModelSB.updParam(event.getObject(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }

        paramList = genModelSB.getParam(selectedObjNode.getData().getMyId());
    }

    /**
     * Обработчик сохранения изменения строки для таблицы аналоговых
     *
     * @param event событие
     */
    public void onRowEditParamProp(RowEditEvent<ParamProp> event) {
        logger.log(Level.INFO, "Update row {0}", event.getObject());

        try {
            genModelSB.updParamProp(event.getObject(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }

        paramPropList = genModelSB.getParamProp(selectedObjNode.getParent().getData().getMyId(), selectedObjNode.getData().getMyId());
    }

    /**
     * Обработчик сохранения изменения строки для таблицы перечислимых
     *
     * @param event событие
     */
    public void onRowEditParamPropPer(RowEditEvent<ParamPropPer> event) {
        logger.info("update row " + event.getObject());

        try {
            genModelSB.updParamPropPer(event.getObject(), utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }

        paramPropPerList = genModelSB.getParamPropPer(selectedObjNode.getParent().getData().getMyId(), selectedObjNode.getData().getMyId());
    }

    /**
     * Метод обрабатывает закрытие окна создания формулы
     */
    public void onCreateNewFormulaDialogClose() {
        newPropCalc = new ParamPropCalc();
    }

    public void onCheckFormulaWrapper() {
        PrimeFaces.current().executeScript("checkFormula();");
    }

    /**
     * Метод проверяет правильность ввода формулы
     */
    public void onCheckFormula() {
        try {
            List<String> variables = genModelSB.checkNewFormula(newPropCalc.getFormula());

            if (variables.size() <= 1) {
                throw new SystemParamException("Формула введена неверно. Проверьте правильность ввода.");
            }

            newPropCalc.setProps(new ArrayList<>());
            for (String var: variables) {
                newPropCalc.addParamProp(var);
            }

            newPropCalc.setParamListForChoice(genModelSB.getParamList());

            List<StatAggrTable> statAgrList = genModelSB.getStatAgrList(newPropCalc.getParamListForChoice().get(0).getId());
            newPropCalc.getProps().forEach(propRow -> propRow.setStatAggrListForChoice(statAgrList));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Метод подгружает списки статистических агрегатов в зависимости от выбранного параметра
     * @param index номер переменной
     */
    public void onParamSelect(int index) {
        newPropCalc.getProps().get(index).setStatAggrListForChoice(genModelSB.getStatAgrList(newPropCalc.getProps().get(index).getParam().getId()));
    }

    /**
     * Сохранение формулы
     */
    public void onSaveFormula() {
        logger.log(Level.INFO, "Save formula {0}", newPropCalc);

        long parentMyId = selectedObjNode.getParent().getData().getMyId();
        long myId = selectedObjNode.getData().getMyId();

        try {
            genModelSB.addCalcAgrFormula(parentMyId, myId, newPropCalc, utilMB.getLogin(), utilMB.getIp());

            PrimeFaces.current().executeScript("PF('addNewFormula').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }

        propCalc = genModelSB.getParamPropCalc(parentMyId, myId);
    }


    /**
     * Обработчик удаления формулы, нажатие на копку -
     */
    public void onRemoveFormula() {
        logger.info("remove calc arg formula");

        long parentMyId = selectedObjNode.getParent().getData().getMyId();
        long myId = selectedObjNode.getData().getMyId();

        try {
            genModelSB.removeCalcAgrFormula(parentMyId, myId, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }

        propCalc = genModelSB.getParamPropCalc(parentMyId, myId);
    }

    public TreeNode<GMTree> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<GMTree> root) {
        this.root = root;
    }

    public TreeNode<GMTree> getSelectedObjNode() {
        return selectedObjNode;
    }

    public void setSelectedObjNode(TreeNode<GMTree> selectedObjNode) {
        this.selectedObjNode = selectedObjNode;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }

    public void setDisableRemoveBtn(boolean disableRemoveBtn) {
        this.disableRemoveBtn = disableRemoveBtn;
    }

    public Param getParamList() {
        return paramList;
    }

    public void setParamList(Param paramList) {
        this.paramList = paramList;
    }

    public List<ParamProp> getParamPropList() {
        return paramPropList;
    }

    public void setParamPropList(List<ParamProp> paramPropList) {
        this.paramPropList = paramPropList;
    }

    public List<ParamPropPer> getParamPropPerList() {
        return paramPropPerList;
    }

    public void setParamPropPerList(List<ParamPropPer> paramPropPerList) {
        this.paramPropPerList = paramPropPerList;
    }

    public boolean isRenderParamTable() {
        return renderParamTable;
    }

    public void setRenderParamTable(boolean renderParamTable) {
        this.renderParamTable = renderParamTable;
    }

    public boolean isRenderParamPropTable() {
        return renderParamPropTable;
    }

    public void setRenderParamPropTable(boolean renderParamPropTable) {
        this.renderParamPropTable = renderParamPropTable;
    }

    public boolean isRenderParamPropPerTable() {
        return renderParamPropPerTable;
    }

    public void setRenderParamPropPerTable(boolean renderParamPropPerTable) {
        this.renderParamPropPerTable = renderParamPropPerTable;
    }

    public boolean isRenderCalcAgrVars() {
        return renderCalcAgrVars;
    }

    public void setRenderCalcAgrVars(boolean renderCalcAgrVars) {
        this.renderCalcAgrVars = renderCalcAgrVars;
    }

    public String getCalcAgrFormulaString() {
        return "Формула" + (propCalc.getFormula() != null ? ": " + propCalc.getFormula() : "");
    }

    public Param getAddParam() {
        return addParam;
    }

    public void setAddParam(Param addParam) {
        this.addParam = addParam;
    }

    public boolean isDisableAddBtn() {
        return disableAddBtn;
    }

    public void setDisableAddBtn(boolean disableAddBtn) {
        this.disableAddBtn = disableAddBtn;
    }

    public String getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(String tableHeader) {
        this.tableHeader = tableHeader;
    }

    public List<Temperature> getGraphOrDecreaseList() {
        return graphOrDecreaseList;
    }

    public void setGraphOrDecreaseList(List<Temperature> graphOrDecreaseList) {
        this.graphOrDecreaseList = graphOrDecreaseList;
    }

    public boolean isGraphRender() {
        return temperatureStatus == TemperatureStatus.GRAPH;
    }

    public boolean isDecreaseRender() {
        return temperatureStatus == TemperatureStatus.DAILY_REDUCTION;
    }

    public boolean isOptValueRender() {
        return temperatureStatus == TemperatureStatus.OPT_VALUE;
    }

    public TreeNode<GMTree> getRootFiltered() {
        return rootFiltered;
    }

    public void setRootFiltered(TreeNode<GMTree> rootFiltered) {
        this.rootFiltered = rootFiltered;
    }

    public boolean isDisableAddFormulaBtn() {
        return propCalc.getFormula() != null;
    }

    public ParamPropCalc getPropCalc() {
        return propCalc;
    }

    public ParamPropCalc getNewPropCalc() {
        return newPropCalc;
    }

    public void setNewPropCalc(ParamPropCalc newPropCalc) {
        this.newPropCalc = newPropCalc;
    }
}