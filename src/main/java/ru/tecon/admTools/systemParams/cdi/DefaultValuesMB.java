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

    private String selectedDataBase;
    private List<String> dataBaseList = new LinkedList<>();

    @EJB
    private DefaultValuesSB defaultValuesBean;

    @Inject
    private DefaultValuesSessionMB valuesSessionMB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        loadDataBaseList();

        selectedObjectType = valuesSessionMB.getDefaultObjectType();
    }

    /**
     * Загруска списка баз данных
     */
    private void loadDataBaseList() {
        dataBaseList = defaultValuesBean.getDataBaseList();

        try {
            String defaultDataBase = defaultValuesBean.getDefaultDataBase();

            dataBaseList.remove(defaultDataBase);
            dataBaseList.add(0, defaultDataBase);
        } catch (SystemParamException e) {
            LOGGER.log(Level.WARNING, "error load default data base", e);
        }
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

    /**
     * Обработка сохранения нового имени базы по умолчанию
     */
    public void onUpdateDefaultDataBase() {
        LOGGER.info("update default data base " + selectedDataBase);

        try {
            defaultValuesBean.updateDefaultDataBase(selectedDataBase, utilMB.getLogin(), utilMB.getIp());

            loadDataBaseList();
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

    public List<String> getDataBaseList() {
        return dataBaseList;
    }

    public String getSelectedDataBase() {
        return selectedDataBase;
    }

    public void setSelectedDataBase(String selectedDataBase) {
        this.selectedDataBase = selectedDataBase;
    }
}
