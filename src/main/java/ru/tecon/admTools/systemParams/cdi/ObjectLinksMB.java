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
import javax.faces.view.facelets.FaceletContext;
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

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private ObjectLinksSB objectLinksSB;

    @PostConstruct
    private void init() {
        objectLinks = objectLinksSB.getObjectLinks();
        objectTypes = objectLinksSB.getObjectTypes();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
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
                link.setId(objectLinksSB.addObjectLink(link, login, ip));
            } else {
                objectLinksSB.updateObjectLink(link, login, ip);
            }

            selectedLink = null;
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
            objectLinks = objectLinksSB.getObjectLinks();
        }
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
            objectLinksSB.removeObjectLink(selectedLink, login, ip);

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

    public boolean isWrite() {
        return write;
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
