package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.RowEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.GroundTempSB;
import ru.tecon.admTools.systemParams.model.GroundTemp;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы температура грунта
 * @author Maksim Shchelkonogov
 */
@Named("groundTempMB")
@ViewScoped
public class GroundTempMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(GroundTempMB.class.getName());

    private List<GroundTemp> groundTemps = new ArrayList<>();

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private GroundTempSB groundTempSB;

    @PostConstruct
    private void init() {
        groundTemps = groundTempSB.getGroundTemps();

        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance()
                .getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        ip = (String) faceletContext.getAttribute("ip");
        login = (String) faceletContext.getAttribute("login");
        write = (boolean) faceletContext.getAttribute("write");
    }

    /**
     * Обработчик сохранения изменения строки
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<GroundTemp> event) {
        LOGGER.info("update row " + event.getObject());

        GroundTemp groundTemp = event.getObject();

        try {
            groundTempSB.addGroundTemp(groundTemp, login, ip);
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка добавления", e.getMessage()));
        }

        groundTemps = groundTempSB.getGroundTemps();
    }

    /**
     * Обработчик добавления новой температуры грунта, нажатие на копку добавить единицу измерения (+)
     */
    public void onAddNew() {
        groundTemps.add(0, new GroundTemp());
    }

    public boolean isWrite() {
        return write;
    }

    public List<GroundTemp> getGroundTemps() {
        return groundTemps;
    }
}
