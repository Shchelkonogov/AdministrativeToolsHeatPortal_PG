package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.cdi.scope.application.ObjectTypeController;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
