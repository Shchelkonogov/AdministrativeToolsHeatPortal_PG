package ru.tecon.admTools.systemParams.cdi.scope.view;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.ejb.ParamTypeSettingSB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 29.06.2023
 */
@Named("conditionControllerView")
@ViewScoped
public class ConditionController implements Serializable {

    private List<Condition> paramConditions = new ArrayList<>();

    @Inject
    private transient Logger logger;

    @EJB
    private ParamTypeSettingSB bean;

    @PostConstruct
    private void init() {
        logger.info("Load conditions");
        paramConditions = bean.getParamConditions();
    }

    public List<Condition> getParamConditions() {
        return paramConditions;
    }
}
