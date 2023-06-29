package ru.tecon.admTools.systemParams.cdi.scope.view;

import ru.tecon.admTools.systemParams.ejb.ParamTypeSettingSB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
