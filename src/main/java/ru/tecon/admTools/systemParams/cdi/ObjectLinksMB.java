package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.ObjectLinksSB;
import ru.tecon.admTools.systemParams.model.ObjectLink;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы связи объектов
 *
 * @author Maksim Shchelkonogov
 */
@Named("objectLinks")
@ViewScoped
public class ObjectLinksMB implements Serializable, AutoUpdate {

    private List<ObjectLink> objectLinks = new ArrayList<>();
    private ObjectLink selectedLink;

    private boolean disableRemoveBtn = true;

    @Inject
    private transient Logger logger;

    @EJB
    private ObjectLinksSB objectLinksSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        objectLinks = objectLinksSB.getObjectLinks();

        PrimeFaces.current().executeScript("PF('linksTable').filter()");
        PrimeFaces.current().executeScript("PF('linksTable').unselectAllRows()");
    }

    /**
     * Обработчик сохранения изменения строки
     *
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<ObjectLink> event) {
        logger.info("update row " + event.getObject());

        ObjectLink link = event.getObject();

        try {
            if (link.getId() == 0) {
                link.setId(objectLinksSB.addObjectLink(link, utilMB.getLogin(), utilMB.getIp()));
            } else {
                objectLinksSB.updateObjectLink(link, utilMB.getLogin(), utilMB.getIp());
            }

            link.setAllowChange(false);
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
     *
     * @param event событие
     */
    public void onRowSelect(SelectEvent<ObjectLink> event) {
        logger.info("select link: " + event.getObject());

        disableRemoveBtn = event.getObject().getId() == 0;
    }

    /**
     * Обработчик удаления связи, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveLink() {
        logger.info("remove link: " + selectedLink);

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
        ObjectLink newLink = new ObjectLink("Новая связь");
        newLink.setAllowChange(true);
        objectLinks.add(newLink);
    }

    public void onRowEditCancel(RowEditEvent<ObjectLink> event) {
        objectLinks.remove(event.getObject());
        disableRemoveBtn = true;
    }

    public List<ObjectLink> getObjectLinks() {
        return objectLinks;
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
