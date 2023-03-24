package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.DefaultValuesSB;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сессионный котроллер с данными о типах объектов
 * @author Maksim Shchelkonogov
 * 23.03.2023
 */
@Named("defaultValuesSession")
@SessionScoped
public class DefaultValuesSessionMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DefaultValuesSessionMB.class.getName());

    private ObjectType defaultObjectType;
    private List<ObjectType> objectTypes = new LinkedList<>();

    @EJB
    private DefaultValuesSB defaultValuesBean;

    @PostConstruct
    private void init() {
        LOGGER.log(Level.INFO, "init bean");
        loadDefaultTypes();
    }

    /**
     * Загрузка типов объектов
     */
    public void loadDefaultTypes() {
        LOGGER.log(Level.INFO, "load default types data");
        objectTypes = defaultValuesBean.getObjectTypes();
        try {
            int defaultObjectTypeID = defaultValuesBean.getDefaultObjectTypeID();

            defaultObjectType = objectTypes.stream()
                    .filter(objectType -> objectType.getId() == defaultObjectTypeID)
                    .findFirst()
                    .orElseThrow(SystemParamException::new);
        } catch (SystemParamException e) {
            LOGGER.log(Level.WARNING, "error load default object type", e);
        }
    }

    public ObjectType getDefaultObjectType() {
        return defaultObjectType;
    }

    public List<ObjectType> getObjectTypes() {
        return objectTypes;
    }
}
