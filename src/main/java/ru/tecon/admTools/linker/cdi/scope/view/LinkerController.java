package ru.tecon.admTools.linker.cdi.scope.view;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import ru.tecon.admTools.linker.cdi.converter.LinkedDataConverter;
import ru.tecon.admTools.linker.cdi.converter.OpcObjectForLinkDataConverter;
import ru.tecon.admTools.linker.ejb.LinkerStateless;
import ru.tecon.admTools.linker.model.LazyLinkedDataModel;
import ru.tecon.admTools.linker.model.LinkedData;
import ru.tecon.admTools.linker.model.OpcObjectForLinkData;
import ru.tecon.admTools.linker.model.RecountTypes;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.SystemParamsUtilMB;
import ru.tecon.admTools.systemParams.cdi.scope.application.ObjectTypeController;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
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

    private String newMaskName;

    private final LazyLinkedDataModel<OpcObjectForLinkData> opcObjectsForLinkData = new LazyLinkedDataModel<>(
            new OpcObjectForLinkDataConverter(),
            new ArrayList<>()
    );
    private List<OpcObjectForLinkData> selectedOpcObjectsForLink = new ArrayList<>();

    @Inject
    private ObjectTypeController objectTypeController;

    @Inject
    private transient Logger logger;

    @Inject
    private SystemParamsUtilMB utilMB;

    @EJB
    private LinkerStateless linkerBean;

    @PostConstruct
    private void init() {
        // По факту это заглушка для запуска инициализации контроллера SystemParamsUtilMB
        logger.log(Level.INFO, "Init data {0}", utilMB);

        // TODO change
        utilMB.setLogin("appAdmin");

        selectedObjectType = objectTypeController.getDefaultObjectType();
    }

    /**
     * Обработчик изменения выбранной закладки
     *
     * @param event событие изменения закладки
     */
    public void onTabChange(TabChangeEvent<?> event) {
        logger.log(Level.INFO, "Tab changed. Active Tab {0}", event.getTab().getTitle());

        switch (event.getTab().getTitle()) {
            case "Не линкованные объекты":
                linkedData.getData().clear();
                selectedLinkedData = null;

                PrimeFaces.current().ajax().update("linkerForm:linkerTabView:objectTabView:linkedDataTable");
                break;
            case "Линкованные объекты":
                PrimeFaces.current().executeScript("PF('objectTabViewWidget').select(0); " +
                        "PF('objectTabViewWidget').disable(1); " +
                        "PF('objectTabViewWidget').disable(2);");
                break;
            case "Объекты":
                linkedData.setData(linkerBean.getLinkedData(selectedObjectType.getId(), utilMB.getLogin()));
                selectedLinkedData = null;

                PrimeFaces.current().executeScript("PF('objectTabViewWidget').disable(1); " +
                        "PF('objectTabViewWidget').disable(2);");
                break;
            case "Параметры":
                linkedData.getData().clear();
                selectedLinkedData = null;
                break;
            case "Вычислимые параметры":
                linkedData.getData().clear();
                selectedLinkedData = null;
                break;

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
            linkerBean.subscribe(data);
        } catch (SystemParamException e) {
            data.setSubscribed(!data.isSubscribed());

            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Подпись", e.getMessage()));
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
}
