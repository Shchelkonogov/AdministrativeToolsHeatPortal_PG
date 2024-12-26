package ru.tecon.admTools.systemParams.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.scope.application.ObjectTypeController;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.model.ObjectType;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Контроллер для формы значения по умолчанию
 *
 * @author Maksim Shchelkonogov
 */
@Named("defaultValues")
@ViewScoped
public class DefaultValuesMB implements Serializable {

    private ObjectType selectedObjectType;

    @Inject
    private transient Logger logger;

    @EJB
    private DefaultValuesSB defaultValuesBean;

    @Inject
    private ObjectTypeController objectTypeController;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        selectedObjectType = objectTypeController.getDefaultObjectType();
    }

    /**
     * обработка сохранения нового типа объекта по умолчанию
     */
    public void onUpdateDefaultType() {
        logger.info("update default type " + selectedObjectType);

        try {
            defaultValuesBean.updateDefaultObjectType(selectedObjectType, utilMB.getLogin(), utilMB.getIp());

            objectTypeController.loadDefaultTypes();
            selectedObjectType = objectTypeController.getDefaultObjectType();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    public ObjectType getSelectedObjectType() {
        return selectedObjectType;
    }

    public void setSelectedObjectType(ObjectType selectedObjectType) {
        this.selectedObjectType = selectedObjectType;
    }
}
