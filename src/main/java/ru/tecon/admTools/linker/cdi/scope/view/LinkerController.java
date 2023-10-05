package ru.tecon.admTools.linker.cdi.scope.view;

import org.primefaces.PrimeFaces;
import org.primefaces.component.tree.TreeDragDropInfo;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.util.LangUtils;
import ru.tecon.admTools.components.navigation.ejb.NavigationBeanLocal;
import ru.tecon.admTools.components.navigation.model.LazyLoadingTreeNode;
import ru.tecon.admTools.components.navigation.model.TreeNodeModel;
import ru.tecon.admTools.linker.cdi.converter.LinkSchemaDataConverter;
import ru.tecon.admTools.linker.cdi.converter.LinkedDataConverter;
import ru.tecon.admTools.linker.cdi.converter.OpcObjectForLinkDataConverter;
import ru.tecon.admTools.linker.cdi.converter.OpcObjectForNoLinkDataConverter;
import ru.tecon.admTools.linker.ejb.LinkerStateless;
import ru.tecon.admTools.linker.model.*;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;
import ru.tecon.admTools.systemParams.cdi.scope.application.ObjectTypeController;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Контроллер для формы "Линковщик"
 *
 * @author Maksim Shchelkonogov
 * 07.07.2023
 */
@Named("linkerController")
@ViewScoped
public class LinkerController implements Serializable {

    // Закладка "Линкованные объекты / Объекты"

    private ObjectType selectedObjectType;

    private final LazyLinkedDataModel<LinkedData> linkedData = new LazyLinkedDataModel<>(
            new LinkedDataConverter(),
            new ArrayList<>(),
            Stream.of(new Object[][]{
                    {"dbObject.name", SortOrder.ASCENDING},
                    {"opcObject.name", SortOrder.ASCENDING}
            }).collect(Collectors.toMap(k -> (String) k[0], v -> (SortOrder) v[1], (oldValue, newValue) -> oldValue, LinkedHashMap::new))
    );
    private LinkedData selectedLinkedData;
    private Integer selectedLinkedObjectId;

    private String newMaskName;

    private final LazyLinkedDataModel<OpcObjectForLinkData> opcObjectsForLinkData = new LazyLinkedDataModel<>(
            new OpcObjectForLinkDataConverter(),
            new ArrayList<>()
    );
    private List<OpcObjectForLinkData> selectedOpcObjectsForLink = new ArrayList<>();

    // Закладка "Линкованные объекты / Вычислимые параметры"

    private final TreeNode<TreeData> rootCalcTree = new DefaultTreeNode<>(new TreeData(), null);
    private TreeNode<TreeData> selectedCalcTreeNode;

    private String filterCalcTreeValue = "";

    private List<CalcDataTable> calcDataTableList = new ArrayList<>();

    // Закладка "Линкованные объекты / Параметры"

    private final TreeNode<TreeData> rootKmOmTree = new DefaultTreeNode<>(new TreeData(), null);
    private TreeNode<TreeData> selectedParamTreeNode;

    private String filterParamTreeValue = "";
    private String paramTreePanelHeader = "Конкретная модель";

    private final TreeNode<TreeData> rootOpcTree = new DefaultTreeNode<>(new TreeData(), null);
    private TreeNode<TreeData> selectedParamOpcTreeNode;

    private String filterParamOpcTreeValue = "";
    private String paramOpcTreePanelHeader = "Все параметры";

    // Данные для перехода на Источники данных

    private MenuModel redirectMenu;

    // Данные "Нелинкованные объекты"

    private final LazyLinkedDataModel<OpcObjectForLinkData> opcObjectsForNoLinkData = new LazyLinkedDataModel<>(
            new OpcObjectForNoLinkDataConverter(),
            new ArrayList<>()
    );
    private OpcObjectForLinkData selectedOpcObjectsForNoLink = null;

    private LazyLoadingTreeNode<TreeNodeModel> selectedNavigateObject = null;
    private ObjectType selectedNavigateObjectType;

    private final Set<OpcObjectForLinkData> objectsForLink = new HashSet<>();

    private final LazyLinkedDataModel<OpcObjectForLinkData> opcObjectParams = new LazyLinkedDataModel<>(
            new OpcObjectForLinkDataConverter(),
            new ArrayList<>()
    );

    private final LazyLinkedDataModel<LinkSchemaData> linkSchemaTable = new LazyLinkedDataModel<>(
            new LinkSchemaDataConverter(),
            new ArrayList<>()
    );
    private LinkSchemaData selectedLinkSchema = null;

    @Inject
    private ObjectTypeController objectTypeController;

    @Inject
    private transient Logger logger;

    @Inject
    private SystemParamsUtilMB utilMB;

    @EJB
    private LinkerStateless linkerBean;

    @EJB(beanName = "navigationBean")
    private NavigationBeanLocal navigationBean;

    @PostConstruct
    private void init() {
        // По факту это заглушка для запуска инициализации контроллера SystemParamsUtilMB
        logger.log(Level.INFO, "Init data {0}", utilMB);

        selectedObjectType = objectTypeController.getDefaultObjectType();

        PrimeFaces.current().executeScript("PF('objectTabViewWidget').disable(1); PF('objectTabViewWidget').disable(2);");

        // Загрузка данных для redirect (Источники данных)
        redirectMenu = new DefaultMenuModel();

        for (Map.Entry<String, String> entry: linkerBean.getRedirect().entrySet()) {
            DefaultMenuItem menuItem = DefaultMenuItem.builder()
                    .value(entry.getKey())
                    .url(entry.getValue())
                    .target("_blank")
                    .icon("pi pi-external-link")
                    .onclick("changeVisible('redirectForm')")
                    .build();
            redirectMenu.getElements().add(menuItem);
        }

        // Загрузка объектов для линковки
        loadOpcObjectsForNoLink();
        selectedNavigateObjectType = objectTypeController.getDefaultObjectType();
    }

    /**
     * Обработчик изменения выбранной закладки
     *
     * @param event событие изменения закладки
     */
    public void onTabChange(TabChangeEvent<?> event) {
        logger.log(Level.INFO, "Tab changed. Active Tab {0}", event.getTab().getTitle());

        switch (event.getTab().getTitle()) {
            case "Нелинкованные объекты":
                // Загрузка объектов для линковки
                loadOpcObjectsForNoLink();

                // Очищаю вкладку "Линкованные объекты / Объекты"
                linkedData.getData().clear();
                selectedLinkedData = null;
                selectedLinkedObjectId = null;

                // Очищаю вкладку "Линкованные объекты / Вычислимые параметры"
                filterCalcTreeValue = "";
                if (selectedCalcTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('calcTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:calcTree\\\\:" + selectedCalcTreeNode.getRowKey() + "'), false);");
                    selectedCalcTreeNode = null;
                }
                rootCalcTree.getChildren().clear();
                calcDataTableList.clear();

                // Очищаю вкладку "Линкованные объекты / Параметры"
                filterParamTreeValue = "";
                paramTreePanelHeader = "Конкретная модель";
                if (selectedParamTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('paramTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:paramTree\\\\:" + selectedParamTreeNode.getRowKey() + "'), false);");
                    selectedParamTreeNode = null;
                }
                rootKmOmTree.getChildren().clear();
                filterParamOpcTreeValue = "";
                paramOpcTreePanelHeader = "Все параметры";
                if (selectedParamOpcTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('paramOpcTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:paramOpcTree\\\\:" + selectedParamOpcTreeNode.getRowKey() + "'), false);");
                    selectedParamOpcTreeNode = null;
                }
                rootOpcTree.getChildren().clear();

                PrimeFaces.current().ajax().update("linkerForm:linkerTabView:objectTabView:linkedDataTable",
                        "linkerForm:linkerTabView:objectTabView:calcDataTable",
                        "linkerForm:linkerTabView:objectTabView:removeCalcLinkBtn",
                        "linkerForm:linkerTabView:objectTabView:createCalcLinkBtn",
                        "linkerForm:linkerTabView:objectTabView:filterCalcTree",
                        "linkerForm:linkerTabView:objectTabView:filterParamTree",
                        "linkerForm:linkerTabView:objectTabView:filterParamOpcTree",
                        "linkerForm:linkerTabView:objectTabView:paramTreePanelHeader",
                        "linkerForm:linkerTabView:objectTabView:paramOpcTreePanelHeader");

                PrimeFaces.current().executeScript("PF('calcTreeWidget').filter(); " +
                        "PF('paramTreeWidget').filter(); " +
                        "PF('paramOpcTreeWidget').filter(); " +
                        "PF('filterParamTreeSelectOneMenuWidget').selectValue(1); " +
                        "PF('filterParamOpcTreeSelectOneMenuWidget').selectValue(1); " +
                        "PF('objectTabViewWidget').select(0, true); " +
                        "PF('objectTabViewWidget').disable(1); " +
                        "PF('objectTabViewWidget').disable(2); " +
                        "updateEmptyRow(); " +
                        "PF('opcObjectsForNoLinkTableWidget').filter(); " +
                        "reloadNavigate();");
                break;
            case "Линкованные объекты":
                selectedOpcObjectsForNoLink = null;
                opcObjectsForNoLinkData.getData().clear();
                objectsForLink.clear();

                PrimeFaces.current().ajax().update("linkerForm:linkerTabView:paramBtns",
                        "linkerForm:linkerTabView:selectObjects",
                        "linkerForm:linkerTabView:linkBtnForNoLink",
                        "linkerForm:linkerTabView:opcObjectsForNoLinkTable");

                PrimeFaces.current().executeScript("PF('objectTabViewWidget').select(0); " +
                        "PF('objectTabViewWidget').disable(1); " +
                        "PF('objectTabViewWidget').disable(2); " +
                        "clearNavigate();");
                break;
            case "Объекты":
                linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
                selectedLinkedData = null;
                selectedLinkedObjectId = null;

                // Очищаю вкладку "Линкованные объекты / Вычислимые параметры"
                filterCalcTreeValue = "";
                if (selectedCalcTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('calcTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:calcTree\\\\:" + selectedCalcTreeNode.getRowKey() + "'), false);");
                    selectedCalcTreeNode = null;
                }
                rootCalcTree.getChildren().clear();
                calcDataTableList.clear();

                // Очищаю вкладку "Линкованные объекты / Параметры"
                paramTreePanelHeader = "Конкретная модель";
                filterParamTreeValue = "";
                if (selectedParamTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('paramTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:paramTree\\\\:" + selectedParamTreeNode.getRowKey() + "'), false);");
                    selectedParamTreeNode = null;
                }
                rootKmOmTree.getChildren().clear();
                filterParamOpcTreeValue = "";
                paramOpcTreePanelHeader = "Все параметры";
                if (selectedParamOpcTreeNode != null) {
                    PrimeFaces.current().executeScript("PF('paramOpcTreeWidget').unselectNode($('#linkerForm\\\\:linkerTabView\\\\:objectTabView\\\\:paramOpcTree\\\\:" + selectedParamOpcTreeNode.getRowKey() + "'), false);");
                    selectedParamOpcTreeNode = null;
                }
                rootOpcTree.getChildren().clear();

                PrimeFaces.current().ajax().update("linkerForm:linkerTabView:objectTabView:calcDataTable",
                        "linkerForm:linkerTabView:objectTabView:removeCalcLinkBtn",
                        "linkerForm:linkerTabView:objectTabView:createCalcLinkBtn",
                        "linkerForm:linkerTabView:objectTabView:filterCalcTree",
                        "linkerForm:linkerTabView:objectTabView:filterParamTree",
                        "linkerForm:linkerTabView:objectTabView:filterParamOpcTree",
                        "linkerForm:linkerTabView:objectTabView:paramTreePanelHeader",
                        "linkerForm:linkerTabView:objectTabView:paramOpcTreePanelHeader");

                PrimeFaces.current().executeScript("PF('objectTabViewWidget').disable(1); " +
                        "PF('objectTabViewWidget').disable(2); " +
                        "PF('calcTreeWidget').filter(); " +
                        "PF('paramTreeWidget').filter(); " +
                        "PF('paramOpcTreeWidget').filter(); " +
                        "addOnDblClickEvents(); " +
                        "PF('filterParamTreeSelectOneMenuWidget').selectValue(1); " +
                        "PF('filterParamOpcTreeSelectOneMenuWidget').selectValue(1);");
                break;
            case "Параметры":
                linkedData.getData().clear();

                if (selectedLinkedData != null) {
                    selectedLinkedObjectId = selectedLinkedData.getDbObject().getId();

                    selectedLinkedData = null;
                }

                if ((selectedLinkedObjectId != null) && (rootKmOmTree.getChildren().isEmpty())) {
                    prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.KM), rootKmOmTree);

                    prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC), rootOpcTree, null);
                }

                PrimeFaces.current().executeScript("document.getElementById('linkerForm:linkerTabView:objectTabView:paramTree_filter').value = 'init'; " +
                        "PF('paramTreeWidget').filter(); " +
                        "document.getElementById('linkerForm:linkerTabView:objectTabView:paramOpcTree_filter').value = 'init'; " +
                        "PF('paramOpcTreeWidget').filter();");
                break;
            case "Вычислимые параметры":
                linkedData.getData().clear();

                if (selectedLinkedData != null) {
                    selectedLinkedObjectId = selectedLinkedData.getDbObject().getId();

                    selectedLinkedData = null;
                }

                if ((selectedLinkedObjectId != null) && (rootCalcTree.getChildren().isEmpty())) {
                    List<TreeData> treeData = linkerBean.getTreeData(selectedLinkedObjectId, TreeType.CALC);

                    prepareTree(treeData, rootCalcTree);
                }

                PrimeFaces.current().ajax().update("linkerForm:linkerTabView:objectTabView:calcDataTable");
                PrimeFaces.current().executeScript("document.getElementById('linkerForm:linkerTabView:objectTabView:calcTree_filter').value = 'init'; " +
                        "PF('calcTreeWidget').filter();");
                break;
        }
    }

    /**
     * Создание дерева на основе плоского списка данных
     *
     * @param treeData плоский список данных
     * @param root     корневой элемент дерева
     */
    private void prepareTree(List<TreeData> treeData, TreeNode<TreeData> root) {
        prepareTree(treeData, root, "S");
    }

    /**
     * Создание дерева на основе плоского списка данных с указанием идентификатора корневого элемента
     *
     * @param treeData   плоский список данных
     * @param root       корневой элемент дерева
     * @param rootPrefix идентификатор корневого элемента
     */
    private void prepareTree(List<TreeData> treeData, TreeNode<TreeData> root, String rootPrefix) {
        Map<String, TreeNode<TreeData>> nodes = new HashMap<>();
        nodes.put(rootPrefix, root);
        for (TreeData entry: treeData) {
            TreeNode<TreeData> parent = nodes.get(entry.getParent());
            DefaultTreeNode<TreeData> node = new DefaultTreeNode<>(entry.getMyType(), entry, parent);
            nodes.put(entry.getItemId(), node);
        }

        // сортировка дерева
        sortTree(root);

        // раскрытие первого уровня
        for (TreeNode<TreeData> node: root.getChildren()) {
            if (!node.isLeaf()) {
                node.setExpanded(true);
            }
        }
    }

    /**
     * Сортировка дерева по имени
     *
     * @param treeNode узел дерева для сортировки вниз
     */
    private void sortTree(TreeNode<TreeData> treeNode) {
        if (!treeNode.getChildren().isEmpty()) {
            treeNode.getChildren().sort(Comparator.comparing(o -> o.getData().getName()));

            for (int i = 0; i < treeNode.getChildren().size(); i++) {
                treeNode.getChildren().get(i).setRowKey(
                        treeNode.getRowKey().equals("root") ?
                                String.valueOf(i) : treeNode.getRowKey() + "_" + i);
                sortTree(treeNode.getChildren().get(i));
            }
        }
    }

    /**
     * Фильтр дерева в закладке "Линкованные объекты / Вычислимые параметры" и "Линкованные объекты / Параметры"
     *
     * @param treeNode дерево
     * @param ignore   фильтр (не используется)
     * @param locale   locale
     * @return true если проходит фильтрацию
     */
    public boolean customFilter(TreeNode<TreeData> treeNode, Object ignore, Locale locale) {
        if (treeNode.getData() == null) {
            return true;
        }

        String filterText;
        switch (treeNode.getData().getTreeType()) {
            case CALC:
                filterText = filterCalcTreeValue.trim().toLowerCase(locale);
                break;
            case KM:
            case OM:
                filterText = filterParamTreeValue.trim().toLowerCase(locale);
                break;
            case OPC:
                filterText = filterParamOpcTreeValue.trim().toLowerCase(locale);
                break;
            default:
                filterText = "";
        }

        if (LangUtils.isBlank(filterText)) {
            return true;
        }

        return treeNode.getData().getName().toLowerCase(locale).contains(filterText);
    }

    /**
     * Обработчик события drop в деревья на форме "Линкованные объекты / Параметры"
     *
     * @param info событие dragAndDrop
     * @return возвращает разрешен ли dragAndDrop
     */
    public boolean onDrop(TreeDragDropInfo info) {
        TreeData dragNode = (TreeData) info.getDragNode().getData();
        TreeData dropNode = (TreeData) info.getDropNode().getData();

        if (dropNode.getTreeType() != dragNode.getTreeType()) {
            switch (dropNode.getTreeType()) {
                case OPC:
                    if (dragNode.getMyType().equals("NLSA") && dropNode.getMyType().equals("NLOP")) {
                        logger.log(Level.INFO, "Create link for {0} and {1}", new Object[]{dragNode, dropNode});

                        linkParam(info.getDragNode(), info.getDropNode());
                    }
                    break;
                case OM:
                case KM:
                    if (dragNode.getMyType().equals("NLOP") && dropNode.getMyType().equals("NLSA")) {
                        logger.log(Level.INFO, "Create link for {0} and {1}", new Object[]{dropNode, dragNode});

                        linkParam(info.getDropNode(), info.getDragNode());
                    }
                    break;
            }
        }

        return false;
    }

    /**
     * Обработчик выделения объекта автоматизации в таблице "Нелинкованные объекты"
     * @param event событие выделения
     */
    public void onSelectObjectForLink(SelectEvent<OpcObjectForLinkData> event) {
        objectsForLink.add(event.getObject());
    }

    /**
     * Удаление объекта автоматизации из группы для линковки "Нелинкованные объекты"
     */
    public void removeObjectForLink() {
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

        objectsForLink.removeIf(linkData -> linkData.getId().equals(id));
    }

    /**
     * Обработчик удаления объекта автоматизации "Нелинкованные объекты"
     */
    public void removeOpcObject() {
        logger.log(Level.INFO, "remove opc object {0}", selectedOpcObjectsForNoLink);

        try {
            linkerBean.removeOpcObject(selectedOpcObjectsForNoLink);

            selectedOpcObjectsForNoLink = null;
            loadOpcObjectsForNoLink();
            objectsForLink.clear();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Удаление объекта", e.getMessage()));
        }
    }

    /**
     * Очистка параметров выделенного объекта автоматизации "Нелинкованные объекты"
     */
    public void removeOpcObjectParams() {
        logger.log(Level.INFO, "remove opc object params {0}", selectedOpcObjectsForNoLink);

        try {
            linkerBean.removeOpcObjectParams(selectedOpcObjectsForNoLink);

            selectedOpcObjectsForNoLink = null;
            loadOpcObjectsForNoLink();
            objectsForLink.clear();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Удаление параметров", e.getMessage()));
        }
    }

    /**
     * Запрос параметров для выбранного объекта автоматизации "Нелинкованные объекты"
     */
    public void requestOpcParamForNoLink() {
        logger.log(Level.INFO, "request opc params for object {0}", selectedOpcObjectsForNoLink);

        try {
            Future<Void> future = linkerBean.requestOpcParams(selectedOpcObjectsForNoLink.getOpcObject().getName());

            try {
                future.get(1, TimeUnit.MINUTES);
            } catch (InterruptedException | TimeoutException e) {
                throw new SystemParamException("Внутренняя ошибка сервера");
            } catch (ExecutionException e) {
                if (e.getCause() instanceof SystemParamException) {
                    SystemParamException cause = (SystemParamException) e.getCause();
                    throw new SystemParamException(cause.getMessage());
                } else {
                    throw new SystemParamException("Внутренняя ошибка сервера");
                }
            }

            selectedOpcObjectsForNoLink = null;
            loadOpcObjectsForNoLink();
            objectsForLink.clear();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Запрос параметров", e.getMessage()));
        }
    }

    /**
     * Создание фиктивного узла учета "Нелинкованные объекты"
     */
    public void createFictitiousYY() {
        logger.log(Level.INFO, "create fictitious yy {0}", selectedNavigateObject);

        try {
            linkerBean.createFictitiousYY(selectedNavigateObject.getData().getName());

            selectedOpcObjectsForNoLink = null;
            loadOpcObjectsForNoLink();
            objectsForLink.clear();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Создание фиктивного узла учета", e.getMessage()));
        }
    }

    /**
     * Обработчик закрытия окна с просмотром параметров "Нелинкованные объекты"
     */
    public void onShowOpcObjectParamsDialogClose() {
        selectedOpcObjectsForNoLink.setParamCount(opcObjectParams.getData().size());
        int updateIndex = opcObjectsForNoLinkData.getWrappedData().indexOf(selectedOpcObjectsForNoLink);

        PrimeFaces.current().ajax().update("linkerForm:linkerTabView:opcObjectsForNoLinkTable:" + updateIndex + ":paramCount");

        opcObjectParams.getData().clear();
    }

    /**
     * Загрузка данных для окна с просмотром параметров "Нелинкованные объекты"
     */
    public void loadOpcObjectParams() {
        opcObjectParams.setData(linkerBean.getOpcObjectParams(selectedOpcObjectsForNoLink));
    }

    /**
     * Загрузка данных для таблицы объектов автоматизации "Нелинкованные объекты"
     */
    public void loadOpcObjectsForNoLink() {
        opcObjectsForNoLinkData.setData(linkerBean.getOpcObjectsForLink());
    }

    /**
     * Нажатие на кнопку слинковать, загрузка схем линковок "Нелинкованные объекты"
     */
    public void linkNoLinkedObjects() {
        logger.log(Level.INFO, "link no linked object {0} {1}", new Object[]{selectedNavigateObject, objectsForLink});

        try {
            linkSchemaTable.setData(linkerBean.getSchemaListForLink(selectedNavigateObject.getData().getMyId(), new ArrayList<>(objectsForLink)));

            PrimeFaces.current().executeScript("PF('schemaLinkDialogWidget').show(); PF('schemaLinkTableWidget').filter();");
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Загрузка схем линковки", e.getMessage()));
        }
    }

    /**
     * Очистка данных окна выбора схемы линковки "Нелинкованные объекты"
     */
    public void onSchemaLinkDialogClose() {
        linkSchemaTable.getData().clear();
        selectedLinkSchema = null;
    }

    /**
     * Обработка отказа завершать линковку (нажатие на крестик или отмена) "Нелинкованные объекты"
     */
    public void schemaLinkDialogClose() {
        logger.log(Level.INFO, "Close link by schema with \"no\"");
        linkerBean.linkBySchemaNo(selectedNavigateObject.getData().getMyId(), new ArrayList<>(objectsForLink));
    }

    /**
     * Линковка объекта по выбранной схеме "Нелинкованные объекты"
     */
    public void linkBySchemaYes() {
        logger.log(Level.INFO, "Link by schema with \"yes\" {0}", selectedLinkSchema);
        linkerBean.linkBySchemaYes(selectedNavigateObject.getData().getMyId(), new ArrayList<>(objectsForLink), selectedLinkSchema, utilMB.getLogin(), utilMB.getIp());

        selectedOpcObjectsForNoLink = null;
        loadOpcObjectsForNoLink();
        objectsForLink.clear();
    }

    /**
     * Линковка объекта по выбранной схеме без параметров "Нелинкованные объекты"
     */
    public void linkBySchemaNoParam() {
        logger.log(Level.INFO, "Link by schema with \"yes no param\"");
        linkerBean.linkBySchemaNoParam(selectedNavigateObject.getData().getMyId(), new ArrayList<>(objectsForLink), utilMB.getLogin(), utilMB.getIp());

        selectedOpcObjectsForNoLink = null;
        loadOpcObjectsForNoLink();
        objectsForLink.clear();
    }

    /**
     * Линковка двух элементов дерева в форме "Линкованные объекты / Параметры"
     *
     * @param node1 элемент дерева KM/OM
     * @param node2 эелемент дерева OPC
     */
    private void linkParam(TreeNode<?> node1, TreeNode<?> node2) {
        TreeData node1Data = (TreeData) node1.getData();
        TreeData node2Data = (TreeData) node2.getData();

        try {
            String newOpcId = linkerBean.linkParam(selectedLinkedObjectId, node1Data.getItemId(), node2Data.getItemId());

            // Устанавливаем обновленные типы
            node2.setType("LOP");
            node2Data.setMyType("LOP");
            node1.setType("LSA");
            node2Data.setMyType("LSA");

            // Устанавливаем в дерево opc обновленную id
            String newId = node2Data.getItemId().replaceAll("OP\\d+$", "OP" + newOpcId);
            node2Data.setItemId(newId);
            node2Data.setMyId(newId);

            PrimeFaces.current().executeScript("PF('paramTreeWidget').filter(); " +
                    "PF('paramOpcTreeWidget').filter();");

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Линковка", "Успешное создание связи"));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", e.getMessage()));
        }

        PrimeFaces.current().ajax().update("growl");

        PrimeFaces.current().executeScript("$('.ui-tree-draghelper').remove();");
    }

    /**
     * Разрыв связи выделенных параметров в форме "Линкованные объекты / Параметры"
     */
    public void removeParamLink() {
        logger.log(Level.INFO, "Remove link for {0} and {1}", new Object[]{selectedParamTreeNode, selectedParamOpcTreeNode});
        try {
            String newOpcId = linkerBean.unlinkParam(selectedLinkedObjectId, selectedParamTreeNode.getData().getItemId(), selectedParamOpcTreeNode.getData().getItemId());

            // Устанавливаем в дерево opc обновленную id
            selectedParamTreeNode.setType("NLSA");
            selectedParamTreeNode.getData().setMyType("NLSA");
            selectedParamOpcTreeNode.setType("NLOP");
            selectedParamOpcTreeNode.getData().setMyType("NLOP");

            // Устанавливаем в дерево opc обновленную id
            String newId = selectedParamOpcTreeNode.getData().getItemId().replaceAll("OP\\d+$", "OP" + newOpcId);
            selectedParamOpcTreeNode.getData().setItemId(newId);
            selectedParamOpcTreeNode.getData().setMyId(newId);

            PrimeFaces.current().executeScript("PF('paramTreeWidget').filter(); " +
                    "PF('paramOpcTreeWidget').filter();");

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Линковка", "Успешный разрыв связи"));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", e.getMessage()));
        }

        PrimeFaces.current().ajax().update("growl");
    }

    /**
     * Обработчик выделения элементов деревьев в форме "Линкованные объекты / Параметры"
     *
     * @param event событие выделения
     */
    public void onSelectParamTree(NodeSelectEvent event) {
        // Поскольку требуется выделить новую строку, но при этом нельзя запускать ajax,
        //  то пришлось хитро делать выделение.
        // В тихом режиме выключаю выделение по onstart в js.
        // Запуская фильтрацию.
        // Затем запуская js function для тихого выделения и перемещения scroll к выделенному объекту
        //  (тоже хитро, что бы запустилось уже поле onComplete после filter()).

        TreeData treeNodeData = (TreeData) event.getTreeNode().getData();
        switch (treeNodeData.getTreeType()) {
            case OPC:
                if (event.getTreeNode().getType().equals("LOP")) {
                    // find element in "om / km" tree then set expanded, select and scroll to select item
                    logger.log(Level.INFO, "select linked parameter {0} and find linked aggregate in 'om/km' tree", treeNodeData);

                    String omKmParam = linkerBean.findOmKmParam(selectedLinkedObjectId, treeNodeData.getItemId());

                    if (omKmParam == null) {
                        return;
                    }

                    TreeNode<TreeData> node = findNode(rootKmOmTree, omKmParam);

                    if (node != null) {
                        String rowKey = node.getRowKey();

                        while (node != null) {
                            node.setExpanded(true);
                            node = node.getParent();
                        }

                        PrimeFaces.current().executeScript("PF('paramTreeWidget').filter();");

                        PrimeFaces.Ajax ajax = PrimeFaces.current().ajax();
                        ajax.addCallbackParam("command", "updateAfterSelectWrapper");
                        ajax.addCallbackParam("widgetName", "paramTreeWidget");
                        ajax.addCallbackParam("rowKey", rowKey);
                    } else {
                        // Бывает хитрый момент, когда связи нет в таблицы dz_par_dev_link, то надо писать сообщение
                        PrimeFaces.current().ajax().update("growl");

                        FacesContext.getCurrentInstance()
                                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Линковка", "Свяжите параметр с устройством в паспорте объекта"));
                    }
                }
                break;
            case OM:
            case KM:
                if (event.getTreeNode().getType().equals("LSA")) {
                    // find element in "opc" tree then set expanded, select and scroll to select item
                    logger.log(Level.INFO, "select linked aggregate {0} and find linked parameter in 'opc' tree", treeNodeData);

                    String opcParam = linkerBean.findOpcParam(selectedLinkedObjectId, treeNodeData.getItemId());

                    if (opcParam == null) {
                        return;
                    }

                    TreeNode<TreeData> node = findNode(rootOpcTree, opcParam);

                    if (node != null) {
                        String rowKey = node.getRowKey();

                        while (node != null) {
                            node.setExpanded(true);
                            node = node.getParent();
                        }

                        PrimeFaces.current().executeScript("PF('paramOpcTreeWidget').filter();");

                        PrimeFaces.Ajax ajax = PrimeFaces.current().ajax();
                        ajax.addCallbackParam("command", "updateAfterSelectWrapper");
                        ajax.addCallbackParam("widgetName", "paramOpcTreeWidget");
                        ajax.addCallbackParam("rowKey", rowKey);
                    }
                }
                break;
        }
    }

    /**
     * Метод продолжение выделения элемента, что бы было в другом запросе и выполнилось уже после фильтрации
     */
    public void updateParamTreesWrapper() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        PrimeFaces.Ajax ajax = PrimeFaces.current().ajax();
        ajax.addCallbackParam("command", "updateAfterSelect");
        ajax.addCallbackParam("widgetName", params.get("widgetName"));
        ajax.addCallbackParam("rowKey", params.get("rowKey"));

        // Устанавливаем выделенные node
        String rowKey = params.get("rowKey");
        String[] split = rowKey.split("_");
        TreeNode<TreeData> node;
        switch (params.get("widgetName")) {
            case "paramOpcTreeWidget":
                node = rootOpcTree;
                for (String s: split) {
                    node = node.getChildren().get(Integer.parseInt(s));
                }
                setSelectedParamOpcTreeNode(node);
                break;
            case "paramTreeWidget":
                node = rootKmOmTree;
                for (String s: split) {
                    node = node.getChildren().get(Integer.parseInt(s));
                }
                setSelectedParamTreeNode(node);
                break;
        }
    }

    /**
     * Поиск элемента в дереве по шаблону
     *
     * @param node  элемент для начала поиска
     * @param regex шаблон
     * @return найденный элемент
     */
    private TreeNode<TreeData> findNode(TreeNode<TreeData> node, String regex) {
        for (TreeNode<TreeData> child: node.getChildren()) {

            if (child.isLeaf()) {
                if (child.getData().getItemId().matches(regex)) {
                    return child;
                }
            } else {
                TreeNode<TreeData> result = findNode(child, regex);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Обработчик фильтрации дерева OM/KM по типу в форме "Линкованные объекты / Параметры"
     *
     * @param event событие изменение selectOneMenu
     */
    public void onParamTreeModelChange(ValueChangeEvent event) {
        switch (Integer.parseInt(event.getNewValue().toString())) {
            case 1:
                filterParamTreeValue = "";
                rootKmOmTree.getChildren().clear();
                prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.KM), rootKmOmTree);
                paramTreePanelHeader = "Конкретная модель";
                break;
            case 2:
                filterParamTreeValue = "";
                rootKmOmTree.getChildren().clear();
                prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OM), rootKmOmTree);
                paramTreePanelHeader = "Обобщенная модель";
                break;
        }
    }

    /**
     * Обработчик фильтрации дерева OPC по типу в форме "Линкованные объекты / Параметры"
     *
     * @param event событие изменение selectOneMenu
     */
    public void onParamOpcTreeModelChange(ValueChangeEvent event) {
        switch (Integer.parseInt(event.getNewValue().toString())) {
            case 1:
                filterParamOpcTreeValue = "";
                rootOpcTree.getChildren().clear();
                prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC, (short) 2), rootOpcTree, null);
                paramOpcTreePanelHeader = "Все параметры";
                break;
            case 2:
                filterParamOpcTreeValue = "";
                rootOpcTree.getChildren().clear();
                prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC, (short) 1), rootOpcTree, null);
                paramOpcTreePanelHeader = "Линкованные параметры";
                break;
            case 3:
                filterParamOpcTreeValue = "";
                rootOpcTree.getChildren().clear();
                prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC, (short) 0), rootOpcTree, null);
                paramOpcTreePanelHeader = "Нелинкованные параметры";
                break;
        }
    }

    /**
     * Обработчик нажатия кнопки "Обновить" в форме "Линкованные объекты / Параметры"
     */
    public void refreshParamOpcTree() {
        rootOpcTree.getChildren().forEach(node -> linkerBean.readOpcParams(node.getData().getName()));

        filterParamOpcTreeValue = "";
        rootOpcTree.getChildren().clear();
        prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC, (short) 2), rootOpcTree, null);
        paramOpcTreePanelHeader = "Все параметры";
    }

    /**
     * Обработчки нажатия кнопки "Запросить параметры" в форме "Линкованные объекты / Параметры"
     */
    public void requestOpcParam() {
        boolean update = false;
        List<String> errorList = new ArrayList<>();
        List<Future<Void>> requests = new ArrayList<>();

        for (TreeNode<TreeData> node: rootOpcTree.getChildren()) {
            try {
                requests.add(linkerBean.requestOpcParams(node.getData().getName()));
                update = true;
            } catch (SystemParamException e) {
                errorList.add(e.getMessage());
            }
        }

        for (Future<Void> future: requests) {
            try {
                future.get(1, TimeUnit.MINUTES);
            } catch (InterruptedException | TimeoutException e) {
                errorList.add("Внутренняя ошибка сервера");
            } catch (ExecutionException e) {
                if (e.getCause() instanceof SystemParamException) {
                    SystemParamException cause = (SystemParamException) e.getCause();
                    errorList.add(cause.getMessage());
                } else {
                    errorList.add("Внутренняя ошибка сервера");
                }
            }
        }

        if (!errorList.isEmpty()) {
            PrimeFaces.current().ajax().update("growl");

            errorList.forEach(error -> FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", error)));
        }

        if (update) {
            filterParamOpcTreeValue = "";
            rootOpcTree.getChildren().clear();
            prepareTree(linkerBean.getTreeData(selectedLinkedObjectId, TreeType.OPC, (short) 2), rootOpcTree, null);
            paramOpcTreePanelHeader = "Все параметры";

            PrimeFaces.current().executeScript("PF('paramOpcTreeWidget').filter(); " +
                    "unselectNode('paramTreeWidget', false); unselectNode('paramOpcTreeWidget', false); " +
                    "PF('filterParamOpcTreeSelectOneMenuWidget').selectValue(1);");
            PrimeFaces.current().ajax().update("linkerForm:linkerTabView:objectTabView:paramOpcTreePanelHeader",
                    "linkerForm:linkerTabView:objectTabView:filterParamOpcTree");
        }
    }

    /**
     * Обработчик выбора элемента дерева в закладке "Линкованные объекты / Вычислимые параметры"
     *
     * @param event событие выделения
     */
    public void onCalcTreeNodeSelect(NodeSelectEvent event) {
        List<String> types = Arrays.asList("LSA", "NLBOP", "NLSA");
        if (event.getTreeNode().isLeaf() && types.contains(event.getTreeNode().getType())) {
            logger.log(Level.INFO, "select tree node item {0}", event.getTreeNode());

            try {
                SystemParam systemParam = ((TreeData) event.getTreeNode().getData()).getSystemParam();

                calcDataTableList = linkerBean.getCalcParamTable(selectedLinkedObjectId, systemParam.getId(), systemParam.getAgrId());
            } catch (ParseException e) {
                calcDataTableList.clear();
            }
        } else {
            calcDataTableList.clear();
        }
    }

    /**
     * Метод разрывает связь параметра
     */
    public void removeCalcLink() {
        logger.log(Level.INFO, "remove calc param link for object {0}, param {1}", new Object[]{selectedLinkedObjectId, selectedCalcTreeNode});

        try {
            SystemParam systemParam = selectedCalcTreeNode.getData().getSystemParam();

            linkerBean.unlinkCalcParam(selectedLinkedObjectId, systemParam.getId(), systemParam.getAgrId());

            selectedCalcTreeNode.setType("NLSA");
        } catch (ParseException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", "Внутренняя ошибка сервера"));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", e.getMessage()));
        }
    }

    /**
     * Метод создает связь параметра
     */
    public void createCalcLink() {
        logger.log(Level.INFO, "create calc param link for object {0}, param {1}", new Object[]{selectedLinkedObjectId, selectedCalcTreeNode});

        try {
            SystemParam systemParam = selectedCalcTreeNode.getData().getSystemParam();

            linkerBean.linkCalcParam(selectedLinkedObjectId, systemParam.getId(), systemParam.getAgrId());

            selectedCalcTreeNode.setType("LSA");
        } catch (ParseException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", "Внутренняя ошибка сервера"));
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Линковка", e.getMessage()));
        }
    }

    /**
     * Обработчик изменения типа объекта на закладке "Линкованные объект / Объекты"
     */
    public void onObjectTypeChange() {
        logger.log(Level.INFO, "change object type filter {0}", selectedObjectType);

        linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
        selectedLinkedData = null;
    }

    /**
     * @param event событие выбора строки в таблице "Линкованные объекты / Объекты"
     */
    public void onLinkedRowSelect(SelectEvent<LinkedData> event) {
        logger.log(Level.INFO, "Select linked data {0}", event.getObject());

        linkerBean.loadRecountData(selectedLinkedData);
    }

    /**
     * Обработчик закрытия окна создания маски для схемы линковки
     */
    public void onCreateMaskDialogClose() {
        newMaskName = null;
    }

    /**
     * Обертка для создания маски для схемы линковки
     */
    public void createMaskWrapper() {
        PrimeFaces.current().executeScript("createMaskWrapper();");
    }

    /**
     * Создание маски для схемы линковки
     */
    public void createMask() {
        logger.log(Level.INFO, "Create mask {0} for {1}", new Object[]{newMaskName, selectedLinkedData});

        String oldValue = selectedLinkedData.getSchema().getName();
        selectedLinkedData.getSchema().setName(newMaskName);

        try {
            linkerBean.changeSchemaName(selectedLinkedData);

            // Коллекция всех объектов
            List<LinkedData> allData = ((LazyLinkedDataModel<LinkedData>) linkedData).getData();

            // Коллекция объектов у которых такой же "объект системы" как и в исправляемом
            List<LinkedData> sameDbObjectsList = allData.stream()
                    .filter(entry -> entry.getDbObject().getId() == selectedLinkedData.getDbObject().getId())
                    .collect(Collectors.toList());

            // Коллекция элементов которые надо обновить
            Set<String> updateList = sameDbObjectsList.stream()
                    .map(entry -> {
                        int index = linkedData.getWrappedData().indexOf(entry);
                        if (index != -1) {
                            return "linkerForm:linkerTabView:objectTabView:linkedDataTable:" + index + ":schemaName";
                        } else {
                            return "";
                        }
                    })
                    .collect(Collectors.toSet());

            // Замена значений
            allData.forEach(entry -> {
                if (sameDbObjectsList.contains(entry)) {
                    entry.getSchema().setName(newMaskName);
                }
            });

            PrimeFaces.current().ajax().update(updateList);
            PrimeFaces.current().executeScript("PF('createMaskDialogWidget').hide();");
        } catch (SystemParamException e) {
            selectedLinkedData.getSchema().setName(oldValue);

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Схема", e.getMessage()));
        }
    }

    /**
     * Обработчик выбора перерасчета
     *
     * @param type тип перерасчета
     */
    public void recount(RecountTypes type) {
        try {
            String message = linkerBean.recount(selectedLinkedData, type);

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Перерасчет", message));
        } catch (SystemParamException e) {
            selectedLinkedData.getRecount().put(type.name(), !selectedLinkedData.getRecount().get(type.name()));

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Перерасчет", e.getMessage()));
        }
    }

    /**
     * Загрузка объектов для долинковки
     */
    public void loadOpcObjectsForLink() {
        opcObjectsForLinkData.setData(linkerBean.getOpcObjectsForLink());
    }

    /**
     * Обработчик закрытия окна долинковки
     */
    public void onAddLinkDialogClose() {
        opcObjectsForLinkData.getData().clear();
        selectedOpcObjectsForLink.clear();
    }

    /**
     * Долинковка выбранных объектов
     */
    public void addLink() {
        logger.log(Level.INFO, "add for {0} links {1}", new Object[]{selectedLinkedData, selectedOpcObjectsForLink});

        for (OpcObjectForLinkData entry: selectedOpcObjectsForLink) {
            try {
                linkerBean.addLink(selectedLinkedData, entry, utilMB.getLogin(), utilMB.getIp());
            } catch (SystemParamException e) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка линковки", e.getMessage()));
            }
        }

        linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
        selectedLinkedData = null;
    }

    /**
     * Обработчик удаления линковки. Кнопка "Разлинковать"
     */
    public void removeLink() {
        logger.log(Level.INFO, "remove all link for {0}", selectedLinkedData);

        try {
            linkerBean.removeLink(selectedLinkedData, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка разлинковки", e.getMessage()));
        }

        linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
        selectedLinkedData = null;
    }

    /**
     * Обработчик удаления всех линковок. Кнопка "Разлинковать все"
     */
    public void removeAllLinks() {
        logger.log(Level.INFO, "remove all links for {0}", selectedLinkedData);

        try {
            linkerBean.removeAllLinks(selectedLinkedData, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка разлинковки", e.getMessage()));
        }

        linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
        selectedLinkedData = null;
    }

    /**
     * Обработчик checkBox в таблице "Линкованные объекты / Объекты" отвечающий за статус подписи.
     *
     * @param data данные для изменения подписи
     */
    public void subscribe(LinkedData data) {
        logger.log(Level.INFO, "Change subscribe {0}", data);

        try {
            linkerBean.subscribe(data, utilMB.getLogin());
        } catch (SystemParamException e) {
            data.setSubscribed(!data.isSubscribed());

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Подпись", e.getMessage()));
        }
    }

    public String getFullObjectName() {
        if (selectedLinkedObjectId != null) {
            return " (" + linkerBean.getFullObjectName(selectedLinkedObjectId) + ")";
        } else {
            return "";
        }
    }

    public LazyLinkedDataModel<LinkedData> getLinkedData() {
        return linkedData;
    }

    public ObjectType getSelectedObjectType() {
        return selectedObjectType;
    }

    public void setSelectedObjectType(ObjectType selectedObjectType) {
        this.selectedObjectType = selectedObjectType;
    }

    public LinkedData getSelectedLinkedData() {
        return selectedLinkedData;
    }

    public void setSelectedLinkedData(LinkedData selectedLinkedData) {
        this.selectedLinkedData = selectedLinkedData;
    }

    public boolean isDisableLinksBtn() {
        return selectedLinkedData == null;
    }

    public boolean isEnableCalcLinksBtn() {
        if ((selectedCalcTreeNode != null) && !calcDataTableList.isEmpty()) {
            for (CalcDataTable entry: calcDataTableList) {
                if (entry.getColor() == 0) {
                    return false;
                }
            }

            return selectedCalcTreeNode.getType().equals("NLSA");
        } else {
            return false;
        }
    }

    public boolean isEnableCalcUnlinksBtn() {
        if (selectedCalcTreeNode != null) {
            return selectedCalcTreeNode.getType().equals("LSA");
        } else {
            return false;
        }
    }

    public String getNewMaskName() {
        return newMaskName;
    }

    public void setNewMaskName(String newMaskName) {
        this.newMaskName = newMaskName;
    }

    public LazyDataModel<OpcObjectForLinkData> getOpcObjectsForLinkData() {
        return opcObjectsForLinkData;
    }

    public List<OpcObjectForLinkData> getSelectedOpcObjectsForLink() {
        return selectedOpcObjectsForLink;
    }

    public void setSelectedOpcObjectsForLink(List<OpcObjectForLinkData> selectedOpcObjectsForLink) {
        this.selectedOpcObjectsForLink = selectedOpcObjectsForLink;
    }

    public TreeNode<TreeData> getRootCalcTree() {
        return rootCalcTree;
    }

    public TreeNode<TreeData> getSelectedCalcTreeNode() {
        return selectedCalcTreeNode;
    }

    public void setSelectedCalcTreeNode(TreeNode<TreeData> selectedCalcTreeNode) {
        this.selectedCalcTreeNode = selectedCalcTreeNode;
    }

    public String getFilterCalcTreeValue() {
        return filterCalcTreeValue;
    }

    public void setFilterCalcTreeValue(String filterCalcTreeValue) {
        this.filterCalcTreeValue = filterCalcTreeValue;
    }

    public List<CalcDataTable> getCalcDataTableList() {
        return calcDataTableList;
    }

    public TreeNode<TreeData> getRootKmOmTree() {
        return rootKmOmTree;
    }

    public String getFilterParamTreeValue() {
        return filterParamTreeValue;
    }

    public void setFilterParamTreeValue(String filterParamTreeValue) {
        this.filterParamTreeValue = filterParamTreeValue;
    }

    public TreeNode<TreeData> getSelectedParamTreeNode() {
        return selectedParamTreeNode;
    }

    public void setSelectedParamTreeNode(TreeNode<TreeData> selectedParamTreeNode) {
        this.selectedParamTreeNode = selectedParamTreeNode;
    }

    public String getFilterParamOpcTreeValue() {
        return filterParamOpcTreeValue;
    }

    public void setFilterParamOpcTreeValue(String filterParamOpcTreeValue) {
        this.filterParamOpcTreeValue = filterParamOpcTreeValue;
    }

    public TreeNode<TreeData> getSelectedParamOpcTreeNode() {
        return selectedParamOpcTreeNode;
    }

    public void setSelectedParamOpcTreeNode(TreeNode<TreeData> selectedParamOpcTreeNode) {
        this.selectedParamOpcTreeNode = selectedParamOpcTreeNode;
    }

    public TreeNode<TreeData> getRootOpcTree() {
        return rootOpcTree;
    }

    public String getParamTreePanelHeader() {
        return paramTreePanelHeader;
    }

    public String getParamOpcTreePanelHeader() {
        return paramOpcTreePanelHeader;
    }

    public MenuModel getRedirectMenu() {
        return redirectMenu;
    }

    public NavigationBeanLocal getNavigationBean() {
        return navigationBean;
    }

    public LazyLinkedDataModel<OpcObjectForLinkData> getOpcObjectsForNoLinkData() {
        return opcObjectsForNoLinkData;
    }

    public OpcObjectForLinkData getSelectedOpcObjectsForNoLink() {
        return selectedOpcObjectsForNoLink;
    }

    public void setSelectedOpcObjectsForNoLink(OpcObjectForLinkData selectedOpcObjectsForNoLink) {
        this.selectedOpcObjectsForNoLink = selectedOpcObjectsForNoLink;
    }

    public LazyLoadingTreeNode<TreeNodeModel> getSelectedNavigateObject() {
        return selectedNavigateObject;
    }

    public void setSelectedNavigateObject(LazyLoadingTreeNode<TreeNodeModel> selectedNavigateObject) {
        this.selectedNavigateObject = selectedNavigateObject;
    }

    public ObjectType getSelectedNavigateObjectType() {
        return selectedNavigateObjectType;
    }

    public void setSelectedNavigateObjectType(ObjectType selectedNavigateObjectType) {
        this.selectedNavigateObjectType = selectedNavigateObjectType;
    }

    public boolean isDisabledParamButtonsForNoLink() {
        return selectedOpcObjectsForNoLink == null;
    }

    public boolean isDisabledLinkBtnForNoLink() {
        return objectsForLink.isEmpty() || (selectedNavigateObject == null) || !selectedNavigateObject.getData().isLeaf();
    }

    public boolean isRenderNavigateCustomBtn() {
        return selectedNavigateObjectType.getCode().equals("УУ");
    }

    public boolean isDisabledNavigateCustomBtn() {
        return (selectedNavigateObject == null) || !selectedNavigateObject.getData().isLeaf();
    }

    public Set<OpcObjectForLinkData> getObjectsForLink() {
        return objectsForLink;
    }

    public LazyLinkedDataModel<OpcObjectForLinkData> getOpcObjectParams() {
        return opcObjectParams;
    }

    public LazyLinkedDataModel<LinkSchemaData> getLinkSchemaTable() {
        return linkSchemaTable;
    }

    public LinkSchemaData getSelectedLinkSchema() {
        return selectedLinkSchema;
    }

    public void setSelectedLinkSchema(LinkSchemaData selectedLinkSchema) {
        this.selectedLinkSchema = selectedLinkSchema;
    }
}
