package ru.tecon.admTools.systemParams.cdi;

import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.systemParams.ejb.IntegrationSB;
import ru.tecon.admTools.systemParams.model.integration.Asot;
import ru.tecon.admTools.systemParams.model.integration.Assd;
import ru.tecon.admTools.systemParams.model.integration.Eod;
import ru.tecon.admTools.systemParams.model.integration.Esm;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
@Named("integrationMB")
@ViewScoped
public class IntegrationMB implements Serializable, AutoUpdate {

    private List<Asot> asot;
    private List<Esm> esm;
    private List<Eod> eod;
    private List<Assd> assd;

    @Inject
    private transient Logger logger;

    @EJB
    private IntegrationSB bean;

    @Override
    public void update() {
        logger.info("init data");
        asot = bean.getAsotData();
        esm = bean.getEsmData();
        eod = bean.getEodData();
        assd = bean.getAssdData();
    }

    public List<Asot> getAsot() {
        return asot;
    }

    public List<Esm> getEsm() {
        return esm;
    }

    public List<Eod> getEod() {
        return eod;
    }

    public List<Assd> getAssd() {
        return assd;
    }
}
