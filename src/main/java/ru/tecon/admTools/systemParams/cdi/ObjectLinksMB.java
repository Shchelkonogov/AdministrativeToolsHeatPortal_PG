package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.ObjectLinksSB;
import ru.tecon.admTools.systemParams.model.ObjectLink;

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
 * Контроллер для формы связи объектов
 * @author Maksim Shchelkonogov
 */
@Named("objectLinks")
@ViewScoped
public class ObjectLinksMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ObjectLinksMB.class.getName());

    private List<ObjectLink> objectLinks = new ArrayList<>();
    private ObjectLink selectedLink;
    private Map<Integer, String> objectTypes = new HashMap<>();

    private boolean disableRemoveBtn = true;

    @EJB
    private ObjectLinksSB objectLinksSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        objectLinks = objectLinksSB.getObjectLinks();
        objectTypes = objectLinksSB.getObjectTypes();
    }

    /**
     * Обработчик сохранения изменения строки
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<ObjectLink> event) {
        LOGGER.info("update row " + event.getObject());

        ObjectLink link = event.getObject();

        try {
            if (link.getId() == 0) {
                link.setId(objectLinksSB.addObjectLink(link, utilMB.getLogin(), utilMB.getIp()));
            } else {
                objectLinksSB.updateObjectLink(link, utilMB.getLogin(), utilMB.getIp());
            }
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
            objectLinks = objectLinksSB.getObjectLinks();
        }

        selectedLink = null;
        disableRemoveBtn = true;
    }

    /**
     * Обработчик события выделения строки
     * @param event событие
     */
    public void onRowSelect(SelectEvent<ObjectLink> event) {
        LOGGER.info("select link: " + event.getObject());

        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления связи, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveLink() {
        LOGGER.info("remove link: " + selectedLink);

        try {
            objectLinksSB.removeObjectLink(selectedLink, utilMB.getLogin(), utilMB.getIp());

            selectedLink = null;
            disableRemoveBtn = true;

            objectLinks = objectLinksSB.getObjectLinks();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик добавления новой связи, нажатие на копку добавить связь (-)
     */
    public void onAddNew() {
        objectLinks.add(new ObjectLink("Новая связь"));
    }

    public List<ObjectLink> getObjectLinks() {
        return objectLinks;
    }

    public Map<Integer, String> getObjectTypes() {
        return objectTypes;
    }

    public Set<Map.Entry<Integer, String>> getObjectTypesAsSet() {
        return getObjectTypes().entrySet();
    }

    public ObjectLink getSelectedLink() {
        return selectedLink;
    }

    public void setSelectedLink(ObjectLink selectedLink) {
        this.selectedLink = selectedLink;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }
}
