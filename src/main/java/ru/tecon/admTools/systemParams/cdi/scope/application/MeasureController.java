package ru.tecon.admTools.systemParams.cdi.scope.application;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.ejb.MeasureSB;
import ru.tecon.admTools.systemParams.model.Measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 03.05.2023
 */
@Named("measureControllerApplication")
@ApplicationScoped
public class MeasureController implements Serializable {

    private List<Measure> measures = new ArrayList<>();

    @Inject
    private Logger logger;

    @EJB
    private MeasureSB measureSB;

    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "load measures");
        measures = measureSB.getMeasures();
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
