package ru.tecon.admTools.systemParams.cdi;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.PlannedOutagesSB;
import ru.tecon.admTools.systemParams.model.plannedOutages.ShutdownRange;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Контроллер для формы плановые отключения
 *
 * @author Aleksey Sergeev
 */
@Named("plannedOutagesMB")
@ViewScoped
public class PlannedOutagesMB implements Serializable {

    private ShutdownRange begShutdownRange;
    private ShutdownRange endShutdownRange;

    @EJB
    private PlannedOutagesSB plannedOutagesSB;

    @PostConstruct
    private void init() {
        begShutdownRange = plannedOutagesSB.getBegShutdownRange();
        endShutdownRange = plannedOutagesSB.getEndShutdownRange();
    }

    /**
     * Обработчик изменения максимального периода до начала отключения
     */
    public void saveNewBegShutdownRange() {
        try {
            plannedOutagesSB.setBegShutdownRange(begShutdownRange.getShutdownRange());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Обработчик изменения максимального периода до конца отключения
     */
    public void saveNewEndShutdownRange() {
        try {
            plannedOutagesSB.setEndShutdownRange(endShutdownRange.getShutdownRange());
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
        }
    }

    /**
     * Обработчик нажатия на кнопку сохранить
     */
    public void save() {
        if (begShutdownRange.isChanged()) {
            saveNewBegShutdownRange();
        }
        if (endShutdownRange.isChanged()) {
            saveNewEndShutdownRange();
        }
    }

    public ShutdownRange getBegShutdownRange() {
        return begShutdownRange;
    }

    public void setBegShutdownRange(ShutdownRange begShutdownRange) {
        this.begShutdownRange = begShutdownRange;
    }

    public ShutdownRange getEndShutdownRange() {
        return endShutdownRange;
    }

    public void setEndShutdownRange(ShutdownRange endShutdownRange) {
        this.endShutdownRange = endShutdownRange;
    }

}
