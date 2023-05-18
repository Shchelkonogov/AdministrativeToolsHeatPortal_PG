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
import ru.tecon.admTools.systemParams.model.temperature.Temperature;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Контроллер для формы обобщенная модель
 * @author Aleksey Sergeev
 */
@Named("genModelMB")
@ViewScoped
public class GenModelMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(GenModelMB.class.getName());

    private TreeNode root;
    private TreeNode selectedObjNode;

    private List<GMTree> objTypesList;
    private GMTree selectedRow;
    private GMTree parent;

    private Param paramList;
    private Param addParam = new Param();
    private boolean disableParamTable = false;
    private List<Temperature> isGraphList;
    private boolean graphRender;
    private List<Temperature> isDecreaseList;
    private boolean decreaseRender;

    private List<ParamProp> paramPropList;
    private boolean disableParamPropTable = false;

    private List<ParamPropPer> paramPropPerList;
    private boolean disableParamPropPerTable = false;

    private List<CalcAgrVars> calcAgrVarsList;
    private String calcAgrFormulaString;
    private String calcAgrFormulaStringForSave;
    private List<CalcAgrFormula> calcAgrFormulaList = new ArrayList<>();

    private List<ParamList> paramListForCalc;

    private List<StatAgrList> statAgrListForCalc;


    private boolean disableCalcAgrVars = false;
    private boolean disableRemoveBtn = true;
    private boolean disableAddBtn = true;
    private boolean addCalcAgr;
    private boolean saveCalcAgr;
    private boolean delCalcAgr;

    private String tableHeader = "Свойства агрегата";


    @EJB
    private GenModelSB genModelSB;
    @EJB(beanName = "tempGraphSB")
    private TemperatureLocal temperatureBean;
    @EJB(beanName = "dailyReductionSB")
    private TemperatureLocal temperatureBean1;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        root = new DefaultTreeNode(new GMTree(), null);

        loadData();
        getGraphList();
        getDecreaseList();
        paramListForCalc = genModelSB.getParamList();
    }

    /**
     * Первоначальная загрузка данных для контроллера
     */
    private void loadData() {

        List<GMTree> expandedList = new ArrayList<>(getRowDown(root));

        root.getChildren().clear();

        objTypesList = genModelSB.getTreeParam();
        Map<String, TreeNode> nodes = new HashMap<>();
        nodes.put(null, root);
        for (GMTree genTree: objTypesList) {
            switch (genTree.getMyType()) {
                case "SA":
                    genTree.setIcon("pi pi-check");
                    break;
                case "PP":
                    genTree.setIcon("pi pi-cog");
                    break;
                default:
                    genTree.setIcon("pi pi-folder");
                    break;
            }
            TreeNode parent = nodes.get(genTree.getParent());
            DefaultTreeNode treeNode = new DefaultTreeNode(genTree, parent);

            if (expandedList.contains(genTree)) {
                treeNode.setExpanded(true);
            }
            nodes.put(genTree.getId(), treeNode);
        }
    }

    /**
     * Метод обрабатывает выбор строки в дереве обощенной модели, для получения данных в таблицы
     * @param event событие выбора строки
     */
    public void onRowSelect(NodeSelectEvent event) throws SystemParamException {
        selectedRow = (GMTree) event.getTreeNode().getData();

        if(selectedRow.getParent()!= null) {
            parent = (GMTree) event.getTreeNode().getParent().getData();
        }
        disableRemoveBtn = true;
        disableAddBtn = true;
        tablesHide();

        LOGGER.info("select obj: " + selectedRow);

        switch (selectedRow.getMyType()) {
            case"PT":
                disableAddBtn = false;
                tableHeader = "Свойства агрегата";
                break;

            /**
             * Для параметра (шестеренка)
             */
            case "PP":
                paramList = genModelSB.getParam(selectedRow.getMyId());
                if (paramList.getIsGraph().getId() != 0) {
                    for (Temperature i: isGraphList) {
                        if (i.getId() == paramList.getIsGraph().getId()) {
                            paramList.setIsGraph(i);
                        }
                    }
                } else {
                    paramList.getIsGraph().setName("нет графика");
                }
                if (paramList.getIsDecrease().getId() != 0) {
                    for (Temperature i: isDecreaseList) {
                        if (i.getId() == paramList.getIsDecrease().getId()) {
                            paramList.setIsDecrease(i);
                        }
                    }
                } else {
                    paramList.getIsDecrease().setName("нет суточного снижения");
                }
                if (paramList.getParamTypeId()!=1) {
                    graphRender = false;
                    decreaseRender = false;
                } else if (paramList.getTechprocTypeId() == 3){
                    graphRender = false;
                    decreaseRender = true;
                } else {
                    graphRender = true;
                    decreaseRender = false;
                }
                disableParamTable = true;
                disableRemoveBtn = false;
                tableHeader = selectedRow.getName();
                break;
        }

        switch (selectedRow.getCateg()) {
            /**
             * Для аналоговых значений
             */
            case"A":
                paramPropList = genModelSB.getParamProp(parent.getMyId(), selectedRow.getMyId());
                disableParamPropTable = true;
                tableHeader = "Свойства агрегата";
                break;

            /**
             * Для перечислимых значений
             */
            case"P":
                paramPropPerList = genModelSB.getParamPropPer(parent.getMyId(), selectedRow.getMyId());
                disableParamPropPerTable = true;
                tableHeader = "Свойства агрегата";
                break;

            /**
             * Для вычислимых значений
             */
            case"F":
                updCalcAgrTable();

                disableCalcAgrVars = true;
                delCalcAgr = !calcAgrVarsList.isEmpty();
                addCalcAgr = calcAgrVarsList.isEmpty();
                saveCalcAgr = false;
                break;
        }
    }
    /**
     * Метод для отрисовки таблиц
     */
    public void tablesHide() {
        disableParamTable = false;
        disableParamPropTable = false;
        disableParamPropPerTable = false;
        disableCalcAgrVars = false;
    }

    /**
     * Метод для загрузки данных в таблицу для вычислимых
     */
    public void updCalcAgrTable() throws SystemParamException {
        paramPropList = genModelSB.getParamProp(parent.getMyId(), selectedRow.getMyId());
        calcAgrVarsList = genModelSB.getCalcAgrVars(parent.getMyId(), selectedRow.getMyId());
        calcAgrFormulaString = genModelSB.getCalcAgrFormula(parent.getMyId(), selectedRow.getMyId());
    }

    /**
     * Обработчик сохранения добавления нового параметра в дерево, нажатие на копку сохранить
     */
    public void onSaveChanges() {
        try {
            Long addedParamID = genModelSB.addParam(parent.getMyId(), selectedRow.getMyId(), addParam, utilMB.getLogin(), utilMB.getIp());

            String Id = "P"+ addedParamID;
            List<GMTree> addedObjList = genModelSB.getTreeParamPP(Id);
            objTypesList.addAll(addedObjList);

            loadData();

            PrimeFaces.current().executeScript("PF('addNewParam').hide();");
            PrimeFaces.current().ajax().update("genModelForm:genModel");
            PrimeFaces.current().ajax().update("genModelTableForm");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
        tablesHide();
        disableParamPropPerTable = true;
    }

    /**
     * Функция составляет список ранее раскрытых объектов в дереве
     * @return - возвращает список открытых объектов
     */
    private List <GMTree> getRowDown(TreeNode treeNode) {
            List<GMTree> expandedList1 = new ArrayList<>();

        for (int i = 0; i < treeNode.getChildCount(); i++) {
            if (!treeNode.getChildren().get(i).isLeaf()) {
                if (treeNode.getChildren().get(i).isExpanded()) {
                    expandedList1.add((GMTree) treeNode.getChildren().get(i).getData());
                }
                expandedList1.addAll(getRowDown(treeNode.getChildren().get(i)));
            }
        }
        return expandedList1;
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
        LOGGER.info("remove param: " + selectedRow.getName());

        try {
            genModelSB.removeParam(selectedRow.getMyId(), utilMB.getLogin(), utilMB.getIp());
            loadData();
            selectedRow = null;
            disableRemoveBtn = true;
            tableHeader = "Свойства агрегата";

            PrimeFaces.current().ajax().update("genModelForm:genModel");
            PrimeFaces.current().ajax().update("genModelTableForm");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
        tablesHide();
        disableParamPropPerTable = true;
    }

    /**
     * Обработчик нажатия на галку в столбце визуализации
     */
    public void changeVisualization(GMTree item) {
        selectedRow = item;
        for (GMTree visParent: objTypesList) {
            if(item.getParent().equals(visParent.getId())) {
                parent = visParent;
            }
        }
        try {
            genModelSB.updParamStat(parent.getMyId(), selectedRow.getMyId(), selectedRow, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка изменения визуализации", e.getMessage()));
        }
    }

    /**
     * Обработчик сохранения изменения строки для таблицы параметров
     * @param event событие
     */
    public void onRowEditParam(RowEditEvent<Param> event) {
        LOGGER.info("update row " + event.getObject());
        Param param = event.getObject();
        try {
            genModelSB.updParam(param, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Обработчик сохранения изменения строки для таблицы аналоговых
     * @param event событие
     */
    public void onRowEditParamProp(RowEditEvent<ParamProp> event) {
        LOGGER.info("update row " + event.getObject());

        ParamProp paramProp = event.getObject();
        try {
            genModelSB.updParamProp(paramProp, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Обработчик сохранения изменения строки для таблицы перечислимых
     * @param event событие
     */
    public void onRowEditParamPropPer(RowEditEvent<ParamPropPer> event) {
        LOGGER.info("update row " + event.getObject());

        ParamPropPer paramPropPer = event.getObject();

        try {
            genModelSB.updParamPropPer(paramPropPer, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Обработчик удаления формулы, нажатие на копку -
     */
    public void onRemoveCalcAgrFormula() {
        LOGGER.info("remove calc arg formula" );

        try {
            Short linked = genModelSB.checkLinkedCalcAgr(parent.getMyId(), selectedRow.getMyId());
            if (linked == 0) {
                try {
                    genModelSB.removeCalcAgrFormula(parent.getMyId(), selectedRow.getMyId(), utilMB.getLogin(), utilMB.getIp());
                    delCalcAgr = false;
                    addCalcAgr = true;
                    saveCalcAgr = false;
                    updCalcAgrTable();
                    PrimeFaces.current().ajax().update("genModelTableForm");
                } catch (SystemParamException e) {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
                }
            }
        }  catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Проверка правильности ввода формулы
     */
    public void checkNewFormula() {
        try {
            List<String> calcAgrFormulaListString = genModelSB.checkNewFormula(calcAgrFormulaString);
            calcAgrFormulaList.clear();
            calcAgrVarsList.clear();
            for (int i = 0; i< calcAgrFormulaListString.size(); i++) {
                ParamList paramList = new ParamList();
                StatAgrList statAgrList = new StatAgrList();
                calcAgrVarsList.add(i, new CalcAgrVars(parent.getMyId(), selectedRow.getMyId(), calcAgrFormulaListString.get(i),
                        paramList, statAgrList));
            }

            if (!calcAgrFormulaString.equals("")) {
                addCalcAgr = false;
                saveCalcAgr = true;
            }
        } catch (SystemParamException e ) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
        calcAgrFormulaStringForSave = calcAgrFormulaString;
    }

    /**
     * Обработчик сохранения добавления новой формулы в вычислимые параметры, нажатие на копку сохранить
     */
    public void onSaveChangesFormula() {
        for (CalcAgrVars i: calcAgrVarsList) {
            calcAgrFormulaList.add(new CalcAgrFormula(i.getVariable(), i.getParamList().getId(), i.getStatAgrList().getStatAgrId()));
        }
        try {
            genModelSB.addCalcAgrFormula(parent.getMyId(), selectedRow.getMyId(), calcAgrFormulaStringForSave,
                    calcAgrFormulaList, utilMB.getLogin(), utilMB.getIp());
            PrimeFaces.current().executeScript("PF('addNewParamF').hide();");
            delCalcAgr = true;
            saveCalcAgr = false;

        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
    }

    /**
     * Обработчик обновления диалогового окна ввода формулы в случае его закрытия
     */
    public void onAddParamDialogCloseF() {
        calcAgrFormulaString = "";
    }

    /**
     * Обработчик выбора значения в выпадающем меню параметров для таблицы вычислимых
     */
    public void paramSelected(CalcAgrVars item, int rowIndex) {
        statAgrListForCalc = genModelSB.getStatAgrList(item.getParamList().getId());
        PrimeFaces.current().ajax().update("genModelTableForm:calcAgrVarsTable:".concat(String.valueOf(rowIndex)).concat(":Memo"));
        PrimeFaces.current().ajax().update("genModelTableForm:calcAgrVarsTable:".concat(String.valueOf(rowIndex)).concat(":paramR"));
        PrimeFaces.current().ajax().update("genModelTableForm:calcAgrVarsTable:".concat(String.valueOf(rowIndex)).concat(":statAgrR"));

        LOGGER.info("Select param "+ item.getParamList().getParName() + " at row " + rowIndex);
    }

    /**
     * Обработчик выбора значения в выпадающем меню статистических агрегатов для таблицы вычислимых
     */
    public void statAgrSelect (CalcAgrVars item, int rowIndex) {
        PrimeFaces.current().ajax().update("genModelTableForm:calcAgrVarsTable:".concat(String.valueOf(rowIndex)).concat(":statAgrR"));
        LOGGER.info("Select statAgr " + item.getStatAgrList().getStatAgrCode() + " at row " + rowIndex);
    }

    /**
     * Метод возвращает список температурных графиков
     */
    public void getGraphList() {
        isGraphList = temperatureBean.getTemperatures();
        isGraphList.add(0, new Temperature(0, "нет графика", ""));
    }

    /**
     * Метод возвращает список суточных снижений
     */
    public void getDecreaseList() {
        isDecreaseList = temperatureBean1.getTemperatures();
        isDecreaseList.add(0, new Temperature(0, "нет суточного снижения",""));
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedObjNode() {
        return selectedObjNode;
    }

    public void setSelectedObjNode(TreeNode selectedObjNode) {
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

    public List<CalcAgrVars> getCalcAgrVarsList() {
        return calcAgrVarsList;
    }

    public void setCalcAgrVarsList(List<CalcAgrVars> calcAgrVarsList) {
        this.calcAgrVarsList = calcAgrVarsList;
    }

    public boolean isDisableParamTable() {
        return disableParamTable;
    }

    public void setDisableParamTable(boolean disableParamTable) {
        this.disableParamTable = disableParamTable;
    }

    public boolean isDisableParamPropTable() {
        return disableParamPropTable;
    }

    public void setDisableParamPropTable(boolean disableParamPropTable) {
        this.disableParamPropTable = disableParamPropTable;
    }

    public boolean isDisableParamPropPerTable() {
        return disableParamPropPerTable;
    }

    public void setDisableParamPropPerTable(boolean disableParamPropPerTable) {
        this.disableParamPropPerTable = disableParamPropPerTable;
    }

    public boolean isDisableCalcAgrVars() {
        return disableCalcAgrVars;
    }

    public void setDisableCalcAgrVars(boolean disableCalcAgrVars) {
        this.disableCalcAgrVars = disableCalcAgrVars;
    }

    public String getCalcAgrFormulaString() {
        return calcAgrFormulaString;
    }

    public void setCalcAgrFormulaString(String calcAgrFormulaString) {
        this.calcAgrFormulaString = calcAgrFormulaString;
    }

    public Param getAddParam() {
        return addParam;
    }

    public void setAddParam(Param addParam) {
        this.addParam = addParam;
    }

    public List<ParamList> getParamListForCalc() {
        return paramListForCalc;
    }

    public void setParamListForCalc(List<ParamList> paramListForCalc) {
        this.paramListForCalc = paramListForCalc;
    }

    public List<StatAgrList> getStatAgrListForCalc() {
        return statAgrListForCalc;
    }

    public void setStatAgrListForCalc(List<StatAgrList> statAgrListForCalc) {
        this.statAgrListForCalc = statAgrListForCalc;
    }

    public boolean isAddCalcAgr() {
        return addCalcAgr;
    }

    public void setAddCalcAgr(boolean addCalcAgr) {
        this.addCalcAgr = addCalcAgr;
    }

    public boolean isDelCalcAgr() {
        return delCalcAgr;
    }

    public void setDelCalcAgr(boolean delCalcAgr) {
        this.delCalcAgr = delCalcAgr;
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

    public boolean isSaveCalcAgr() {
        return saveCalcAgr;
    }

    public void setSaveCalcAgr(boolean saveCalcAgr) {
        this.saveCalcAgr = saveCalcAgr;
    }

    public List<Temperature> getIsGraphList() {
        return isGraphList;
    }

    public void setIsGraphList(List<Temperature> isGraphList) {
        this.isGraphList = isGraphList;
    }

    public boolean isGraphRender() {
        return graphRender;
    }

    public void setGraphRender(boolean graphRender) {
        this.graphRender = graphRender;
    }

    public List<Temperature> getIsDecreaseList() {
        return isDecreaseList;
    }

    public void setIsDecreaseList(List<Temperature> isDecreaseList) {
        this.isDecreaseList = isDecreaseList;
    }

    public boolean isDecreaseRender() {
        return decreaseRender;
    }

    public void setDecreaseRender(boolean decreaseRender) {
        this.decreaseRender = decreaseRender;
    }
}
