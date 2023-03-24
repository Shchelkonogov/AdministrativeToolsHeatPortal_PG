package ru.tecon.admTools.systemParams.cdi.struct;

import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;
import ru.tecon.admTools.systemParams.cdi.converter.MyConverter;
import ru.tecon.admTools.systemParams.ejb.MeasureSB;
import ru.tecon.admTools.systemParams.ejb.struct.StructCurrentRemote;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.struct.*;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Класс родитель для группы контроллеров категории структура
 * @author Maksim Shchelkonogov
 */
public class StructMB implements Serializable, MyConverter {

    private static final Logger LOGGER = Logger.getLogger(StructMB.class.getName());

    private TreeNode root;

    private List<StructType> structTypes;
    private TreeNode selectedStructNode;
    private StructType selectedStruct;
    private List<StructTypeProp> structTypeProps;
    private StructTypeProp selectedStructProp;

    private StructType newStructType = new StructType();
    private List<StructTypeProp> newStructTypeProps = new ArrayList<>();

    private StructTypeProp newStructTypeProp = new StructTypeProp();

    private List<PropValType> propValTypes;
    private List<PropCat> propCat;
    private List<SpHeader> spHeaders;
    private List<Measure> measures;

    private boolean disableRemoveStructBtn = true;
    private boolean disableRemoveStructPropBtn = true;

    private StructCurrentRemote structCurrentBean;

    private boolean addToRoot = true;

    @EJB
    private StructSB structBean;

    @EJB
    private MeasureSB measureSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    void initForm() {
        root = new DefaultTreeNode(new StructType(), null);

        loadData();
    }

    /**
     * Первоначальная загрузка данных для контроллера
     */
    private void loadData() {
        root.getChildren().clear();

        structTypes = structCurrentBean.getStructTypes();

        Map<Long, TreeNode> nodes = new HashMap<>();
        nodes.put(null, root);
        for (StructType structType: structTypes) {
            TreeNode parent = nodes.get(structType.getParentID());
            DefaultTreeNode treeNode = new DefaultTreeNode(structType, parent);
            nodes.put((long) structType.getId(), treeNode);
        }

        root.getChildren().forEach(treeNode -> treeNode.setExpanded(true));

        structTypeProps = null;
        selectedStruct = null;
        selectedStructProp = null;
        disableRemoveStructBtn = true;
        disableRemoveStructPropBtn = true;
    }

    /**
     * Метод обрабатывает выбор строки в таблице типы, для получения данных в таблицу свойства
     * @param event событие выбора строки
     */
    public void onRowSelect(NodeSelectEvent event) {
        StructType selectedRow = (StructType) event.getTreeNode().getData();
        LOGGER.info("select struct: " + selectedRow);
        structTypeProps = structCurrentBean.getStructTypeProps(selectedRow.getId());
        disableRemoveStructBtn = false;

        selectedStructProp = null;
        disableRemoveStructPropBtn = true;
    }

    /**
     * Обработка выбора строки в таблице свойства
     * @param event событие выбора строки
     */
    public void onRowPropSelect(SelectEvent<StructTypeProp> event) {
        LOGGER.info("select struct property: " + event.getObject());

        disableRemoveStructPropBtn = false;
    }

    /**
     * Метод загружает данные для выпадающих меню формы
     * (типы, категории, единицы измерений, названия справочника для свойств структур)
     */
    public void loadProperties() {
        propValTypes = structBean.getPropValTypes();
        propCat = structBean.getPropCat();
        spHeaders = structBean.getSpHeaders();
        measures = measureSB.getMeasures();
    }

    /**
     * Обработка изменения положения строки в таблице свойства структур
     * @param event событие
     */
    public void onRowReorder(ReorderEvent event) {
        LOGGER.info("change position for property " + structTypeProps.get(event.getToIndex()) + " struct: " + selectedStruct +
                " from " + event.getFromIndex() + " to " + event.getToIndex());
        try {
            structCurrentBean.moveProp(selectedStruct.getId(), structTypeProps.get(event.getToIndex()).getId(), event.getFromIndex(), event.getToIndex());
        } catch (SystemParamException e) {
            structTypeProps = structCurrentBean.getStructTypeProps(selectedStruct.getId());
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка перемещения", e.getMessage()));
            PrimeFaces.current().ajax().update("divisionPanel:divisionPropTable");
        }
    }

    /**
     * Добавляем новое свойство
     */
    public void onAddNew() {
        newStructTypeProps.add(new StructTypeProp("Новое свойство"));
    }

    /**
     * Метод обертка для разнесения вызова диалогового окна для сохранения значения в базу при сохранении новой структуры
     */
    public void saveStructWrapper() {
        PrimeFaces.current().executeScript("saveStructWrapper()");
    }

    /**
     * Метод обертка для разнесения вызова диалогового окна для сохранения значения в базу при сохранении нового свойства
     */
    public void savePropWrapper() {
        PrimeFaces.current().executeScript("savePropWrapper()");
    }

    /**
     * Обработка нажатие на удаление структуры
     */
    public void onRemoveStruct() {
        LOGGER.info("remove struct: " + selectedStruct);

        try {
            structCurrentBean.removeStruct(selectedStruct, utilMB.getLogin(), utilMB.getIp());

            loadData();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработка нажатие на удаление свойства структуры
     */
    public void onRemoveStructProp() {
        LOGGER.info("remove struct property: " + selectedStructProp);

        try {
            structCurrentBean.removeStructProp(selectedStruct.getId(), selectedStructProp, utilMB.getLogin(), utilMB.getIp());

            selectedStructProp = null;
            disableRemoveStructPropBtn = true;

            structTypeProps = structCurrentBean.getStructTypeProps(selectedStruct.getId());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработка нажатия на добавление новой структуры
     */
    public void onCreateStruct() {
        LOGGER.info("create new struct: " + newStructType + " and struct property: " + newStructTypeProps);

        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (!addToRoot) {
                if (selectedStruct == null) {
                    throw new SystemParamException("Не выбран тип к которому добавить новое устройство");
                }
                newStructType.setParentID((long) selectedStruct.getId());
            }
            int structID = structCurrentBean.addStruct(newStructType, utilMB.getLogin(), utilMB.getIp());

            if (newStructTypeProps != null) {
                newStructTypeProps.removeIf(StructTypeProp::check);

                for (StructTypeProp prop: newStructTypeProps) {
                    try {
                        structCurrentBean.addStructProp(structID, prop, utilMB.getLogin(), utilMB.getIp());
                    } catch (SystemParamException e) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
                    }
                }
            }

            loadData();

            PrimeFaces.current().executeScript("PF('addDialog').hide();");
        } catch (SystemParamException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработка нажатия на добавление нового свойства структуры
     */
    public void onCreateDivisionProp() {
        LOGGER.info("create new struct property: " + newStructTypeProp + " for struct: " + selectedStruct);

        try {
            structCurrentBean.addStructProp(selectedStruct.getId(), newStructTypeProp, utilMB.getLogin(), utilMB.getIp());

            structTypeProps = structCurrentBean.getStructTypeProps(selectedStruct.getId());
            selectedStructProp = null;
            disableRemoveStructPropBtn = true;

            PrimeFaces.current().executeScript("PF('addPropDialog').hide();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }
    }

    /**
     * Обработка закрытия диалогово окна добавления свойств структуры
     */
    public void onAddPropDialogClose() {
        newStructTypeProp = new StructTypeProp();
    }

    /**
     * Обработка закрытия диалогово окна добавления структуры
     */
    public void onclose() {
        newStructType = new StructType();
        newStructTypeProps.clear();
    }

    public StructType getNewStructType() {
        return newStructType;
    }

    public List<StructTypeProp> getStructTypeProps() {
        return structTypeProps;
    }

    public List<StructType> getStructTypes() {
        return structTypes;
    }

    public TreeNode getSelectedStructNode() {
        return selectedStructNode;
    }

    public void setSelectedStructNode(TreeNode selectedStructNode) {
        this.selectedStructNode = selectedStructNode;
        if (selectedStructNode != null) {
            this.selectedStruct = (StructType) selectedStructNode.getData();
        } else {
            this.selectedStruct = null;
        }
    }

    public List<StructTypeProp> getNewStructTypeProps() {
        return newStructTypeProps;
    }

    public List<PropValType> getPropValTypes() {
        return propValTypes;
    }

    public List<PropCat> getPropCat() {
        return propCat;
    }

    public List<SpHeader> getSpHeaders() {
        return spHeaders;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public boolean isDisableRemoveStructBtn() {
        return disableRemoveStructBtn;
    }

    public boolean isDisableRemoveStructPropBtn() {
        return disableRemoveStructPropBtn;
    }

    public StructTypeProp getSelectedStructProp() {
        return selectedStructProp;
    }

    public void setSelectedStructProp(StructTypeProp selectedStructProp) {
        this.selectedStructProp = selectedStructProp;
    }

    public StructTypeProp getNewStructTypeProp() {
        return newStructTypeProp;
    }

    void setStructCurrentBean(StructCurrentRemote structCurrentBean) {
        this.structCurrentBean = structCurrentBean;
    }

    void setStructBean(StructSB structBean) {
        this.structBean = structBean;
    }

    public TreeNode getRoot() {
        return root;
    }

    /**
     * Устанавливаем параметр способа добавления Типа в структуре
     * @param addToRoot true - добавлять в таблицу (к корневому элементу)
     *                  false - добавлять в дерево (к выбранному элементу)
     */
    public void setAddToRoot(boolean addToRoot) {
        this.addToRoot = addToRoot;
    }
}
