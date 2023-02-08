package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Контроллер для формы значения по умолчанию
 * @author Maksim Shchelkonogov
 */
@Named("defaultValues")
@ViewScoped
public class DefaultValuesMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DefaultValuesMB.class.getName());

    private ObjectType selectedObjectType;
    private List<ObjectType> objectTypes = new LinkedList<>();

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private DefaultValuesSB defaultValuesBean;

    @PostConstruct
    private void init() {
        loadDefaultTypes();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
    }

    /**
     * Загрузка типов объектов
     */
    private void loadDefaultTypes() {
        objectTypes = defaultValuesBean.getObjectTypes();
        try {
            int defaultObjectTypeID = defaultValuesBean.getDefaultObjectTypeID();

            ObjectType defaultObjectType = objectTypes.stream()
                    .filter(objectType -> objectType.getId() == defaultObjectTypeID)
                    .findFirst()
                    .orElseThrow(SystemParamException::new);

            objectTypes.remove(defaultObjectType);
            objectTypes.add(0, defaultObjectType);
        } catch (SystemParamException e) {
            LOGGER.log(Level.WARNING, "error load default object type", e);
        }
    }

    /**
     * обработка сохранения нового типа объекта по умолчанию
     */
    public void onUpdateDefaultType() {
        LOGGER.info("update default type " + selectedObjectType);

        try {
            defaultValuesBean.updateDefaultObjectType(selectedObjectType, login, ip);

            loadDefaultTypes();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    public boolean isWrite() {
        return write;
    }

    public List<ObjectType> getObjectTypes() {
        return objectTypes;
    }

    public ObjectType getSelectedObjectType() {
        return selectedObjectType;
    }

    public void setSelectedObjectType(ObjectType selectedObjectType) {
        this.selectedObjectType = selectedObjectType;
    }
}
