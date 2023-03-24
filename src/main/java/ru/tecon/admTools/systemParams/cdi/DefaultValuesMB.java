package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
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

    @EJB
    private DefaultValuesSB defaultValuesBean;

    @Inject
    private DefaultValuesSessionMB valuesSessionMB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        loadDefaultTypes();

        selectedObjectType = valuesSessionMB.getDefaultObjectType();
    }

    /**
     * обработка сохранения нового типа объекта по умолчанию
     */
    public void onUpdateDefaultType() {
        LOGGER.info("update default type " + selectedObjectType);

        try {
            defaultValuesBean.updateDefaultObjectType(selectedObjectType, utilMB.getLogin(), utilMB.getIp());

            valuesSessionMB.loadDefaultTypes();
            selectedObjectType = valuesSessionMB.getDefaultObjectType();
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
