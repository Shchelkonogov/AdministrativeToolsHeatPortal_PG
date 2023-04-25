package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.GenModelSB;
import ru.tecon.admTools.systemParams.model.genModel.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private List<Param> paramList;
    private Param param = new Param();
    private Param addParam = new Param();
    private boolean disableParamTable = false;

    private List<ParamProp> paramPropList;
    private ParamProp paramProp = new ParamProp();
    private boolean disableParamPropTable = false;

    private List<ParamPropPer> paramPropPerList;
    private ParamPropPer paramPropPer = new ParamPropPer();
    private boolean disableParamPropPerTable = false;

    private List<CalcAgrVars> calcAgrVarsList;
    private String calcAgrFormulaString;
    private CalcAgrVars calcAgrVars = new CalcAgrVars();

    private List<CalcAgrFormula> calcAgrFormulaList = new ArrayList<>();
    private List<String> calcAgrFormulaListString;

    private List<ParamList> paramListList;
    private ParamList selectedParamList;

    private List<StatAgrList> statAgrListList = new ArrayList<>();
    private StatAgrList selectedStatAgrList;

    private boolean disableCalcAgrVars = false;
    private boolean disableRemoveBtn = true;
    private boolean disableAddBtn = true;
    private boolean tableRender;
    private boolean addCalcAgr;
    private boolean saveCalcAgr;
    private boolean delCalcAgr;
    private String rowKeyParent;
    private String rowKey;


    @EJB
    GenModelSB genModelSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        root = new DefaultTreeNode(new GMTree(), null);
        loadData();
    }

    /**
     * Первоначальная загрузка данных для контроллера
     */
    private void loadData() {
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
            nodes.put(genTree.getId(), treeNode);
        }
    }

    /**
     * Метод обрабатывает выбор строки в дереве обощенной модели, для получения данных в таблицы
     * @param event событие выбора строки
     */
    public void onRowSelect(NodeSelectEvent event) throws SystemParamException {
        selectedRow = (GMTree) event.getTreeNode().getData();
        rowKeyParent = event.getTreeNode().getParent().getRowKey();
        rowKey = event.getTreeNode().getRowKey();

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
            break;

        /**
         * Для параметра (шестеренка)
         */
            case "PP":
            paramList = genModelSB.getParam(selectedRow.getMyId());
            disableParamTable = true;
            disableRemoveBtn = false;
            break;
        }

        switch (selectedRow.getCateg()) {
        /**
         * Для аналоговых значений
         */
            case"A":
            paramPropList = genModelSB.getParamProp(parent.getMyId(), selectedRow.getMyId());
            disableParamPropTable = true;
            break;

        /**
         * Для перечислимых значений
         */
            case"P":
            paramPropPerList = genModelSB.getParamPropPer(parent.getMyId(), selectedRow.getMyId());
            disableParamPropPerTable = true;
            break;

        /**
         * Для вычислимых значений
         */
            case"F":
            updCalcAgrTable();

            disableCalcAgrVars = true;
            tableRender = false;
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
        tableRender = true;
    }

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

            String[] array = rowKey.split("\\D+");
            int rowAtTree = Integer.parseInt(String.join("", array))%10;

            TreeNode treeNode = root.getChildren().get(Integer.parseInt(rowKeyParent)).getChildren().get(rowAtTree);
            GMTree parentGMTree = addedObjList.get(0);
            parentGMTree.setIcon("pi pi-cog");
            DefaultTreeNode parent = new DefaultTreeNode(parentGMTree, treeNode);
            addedObjList.remove(parentGMTree);
            for (GMTree gmTree : addedObjList) {
                gmTree.setIcon("pi pi-check");
                new DefaultTreeNode(gmTree, parent);
            }

            PrimeFaces.current().executeScript("PF('addNewParam').hide();");
            PrimeFaces.current().ajax().update("genModelForm:genModel");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
        tablesHide();
        disableParamPropPerTable = true;
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

            PrimeFaces.current().ajax().update("genModelForm:genModel");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
        tablesHide();
        disableParamPropPerTable = true;
        tableRender = false;
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
            if(linked == 0) {
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
            calcAgrFormulaListString = genModelSB.checkNewFormula(calcAgrFormulaString);
            calcAgrFormulaList.clear();
            calcAgrVarsList.clear();
            for (int i = 0; i<calcAgrFormulaListString.size(); i++) {
                ParamList paramList = new ParamList();
                StatAgrList statAgrList = new StatAgrList();
                calcAgrVarsList.add(i, new CalcAgrVars(parent.getMyId(), selectedRow.getMyId(), calcAgrFormulaListString.get(i),
                        paramList, statAgrList));
            }

            paramListTable();
            addCalcAgr = false;
            saveCalcAgr = true;
        } catch (SystemParamException e ) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
    }

    /**
     * Обработчик сохранения добавления новой формулы в вычислимые параметры, нажатие на копку сохранить
     */
    public void onSaveChangesFormula() {
        for(CalcAgrVars i: calcAgrVarsList) {
            calcAgrFormulaList.add(new CalcAgrFormula(i.getVariable(), i.getParamList().getId(), i.getStatAgrList().getStatAgrId()));
        }
        try {
            genModelSB.addCalcAgrFormula(parent.getMyId(), selectedRow.getMyId(), calcAgrFormulaString,
                    calcAgrFormulaList, utilMB.getLogin(), utilMB.getIp());
            PrimeFaces.current().executeScript("PF('addNewParamF').hide();");
            delCalcAgr = true;
            saveCalcAgr = false;


        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage()));
            PrimeFaces.current().ajax().update("growl");
        }
        loadData();
    }

    /**
     * Обработчик обновления диалогового окна ввода формулы в случае его закрытия
     */
    public void onAddParamDialogCloseF() {
        if(calcAgrFormulaListString.isEmpty()) {
            calcAgrFormulaString = "";
        }
    }

    /**
     * Обработчик загрузки параметров в выпадающее меню для таблицы вычислимых
     */
    public void paramListTable() {
        paramListList = genModelSB.getParamList();
    }

    /**
     * Обработчик события выделения значения в выпадающем меню параметров для таблицы вычислимых
     * @param event событие
     */
    public void selectParamList(final AjaxBehaviorEvent event) {
        LOGGER.info("select link: onRowSelectParamList" + ((SelectOneMenu) event.getSource()).getValue());
        selectedParamList = (ParamList) ((SelectOneMenu) event.getSource()).getValue();
        calcAgrVars.setParamList((ParamList) ((SelectOneMenu) event.getSource()).getValue());
    }

    /**
     * Обработчик загрузки стат.агрегат для выбранного параметра в выпадающем меню для таблицы вычислимых
     */

    public void statAgrListTable() {
        if (calcAgrVars.getParamList().getId()!= null) {
            statAgrListList = genModelSB.getStatAgrList(calcAgrVars.getParamList().getId());
        }
    }

    /**
     * Обработчик события выделения строки в таблице параметров в выпадающем меню для таблицы вычислимых
     * @param event событие
     */
    public void selectStatAgrList(final AjaxBehaviorEvent event) {
        LOGGER.info("select link: onRowSelectStatAgrList" + ((SelectOneMenu) event.getSource()).getValue());
        calcAgrVars.setStatAgrList((StatAgrList) ((SelectOneMenu) event.getSource()).getValue());
    }

    /**
     * Обработчик события выделения строки в таблице Вычисление параметра
     * @param event событие
     */
    public void onRowSelectCalcAgrVarsTable(SelectEvent<CalcAgrVars> event) {
        LOGGER.info("select link: onRowSelectCalcAgrVarsTable " + event.getObject());
        calcAgrVars = event.getObject();
        statAgrListTable();
        if (calcAgrVars.getParamList().getId() == null && !statAgrListList.isEmpty()) {
            statAgrListList.clear();
        }
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

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
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

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public ParamProp getParamProp() {
        return paramProp;
    }

    public void setParamProp(ParamProp paramProp) {
        this.paramProp = paramProp;
    }

    public ParamPropPer getParamPropPer() {
        return paramPropPer;
    }

    public void setParamPropPer(ParamPropPer paramPropPer) {
        this.paramPropPer = paramPropPer;
    }

    public CalcAgrVars getCalcAgrVars() {
        return calcAgrVars;
    }

    public void setCalcAgrVars(CalcAgrVars calcAgrVars) {
        this.calcAgrVars = calcAgrVars;
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

    public boolean isTableRender() {
        return tableRender;
    }

    public void setTableRender(boolean tableRender) {
        this.tableRender = tableRender;
    }

    public Param getAddParam() {
        return addParam;
    }

    public void setAddParam(Param addParam) {
        this.addParam = addParam;
    }

    public GMTree getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(GMTree selectedRow) {
        this.selectedRow = selectedRow;
    }

    public List<ParamList> getParamListList() {
        return paramListList;
    }

    public void setParamListList(List<ParamList> paramListList) {
        this.paramListList = paramListList;
    }

    public ParamList getSelectedParamList() {
        return selectedParamList;
    }

    public void setSelectedParamList(ParamList selectedParamList) {
        this.selectedParamList = selectedParamList;
    }

    public List<StatAgrList> getStatAgrListList() {
        return statAgrListList;
    }

    public void setStatAgrListList(List<StatAgrList> statAgrListList) {
        this.statAgrListList = statAgrListList;
    }

    public StatAgrList getSelectedStatAgrList() {
        return selectedStatAgrList;
    }

    public void setSelectedStatAgrList(StatAgrList selectedStatAgrList) {
        this.selectedStatAgrList = selectedStatAgrList;
    }

    public boolean isAddCalcAgr() {
        return addCalcAgr;
    }

    public void setAddCalcAgr(boolean addCalcAgr) {
        this.addCalcAgr = addCalcAgr;
    }

    public boolean isSaveCalcAgr() {
        return saveCalcAgr;
    }

    public void setSaveCalcAgr(boolean saveCalcAgr) {
        this.saveCalcAgr = saveCalcAgr;
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
}
