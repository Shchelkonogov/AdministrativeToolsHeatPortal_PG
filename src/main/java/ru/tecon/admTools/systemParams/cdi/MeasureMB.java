package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MeasureSB;
import ru.tecon.admTools.systemParams.model.Measure;

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
 * Контроллер для формы единицы измерений
 * @author Maksim Shchelkonogov
 */
@Named("measureMB")
@ViewScoped
public class MeasureMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MeasureMB.class.getName());

    private List<Measure> measures = new ArrayList<>();
    private Measure selectedMeasure;

    private boolean disableRemoveBtn = true;

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private MeasureSB measureSB;

    @PostConstruct
    private void init() {
        measures = measureSB.getMeasures();

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
    public void onRowEdit(RowEditEvent<Measure> event) {
        LOGGER.info("update row " + event.getObject());

        Measure measure = event.getObject();

        try {
            if (measure.getId() == 0) {
                measure.setId(measureSB.addMeasure(measure, login, ip));
            } else {
                measureSB.updateMeasure(measure, login, ip);
            }
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка обновления", e.getMessage()));
            measures = measureSB.getMeasures();
        }

        selectedMeasure = null;
        disableRemoveBtn = true;
    }

    /**
     * Обработчик события выделения строки
     * @param event событие
     */
    public void onRowSelect(SelectEvent<Measure> event) {
        LOGGER.info("select link: " + event.getObject());

        disableRemoveBtn = false;
    }

    /**
     * Обработчик удаления единицы измерения, возникает при нажатии на кнопку удалить (-)
     */
    public void onRemoveMeasure() {
        LOGGER.info("remove measure: " + selectedMeasure);

        try {
            measureSB.removeMeasure(selectedMeasure, login, ip);

            selectedMeasure = null;
            disableRemoveBtn = true;

            measures = measureSB.getMeasures();
        } catch (SystemParamException e) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage()));
        }
    }

    /**
     * Обработчик добавления новой единицы измерений, нажатие на копку добавить единицу измерения (+)
     */
    public void onAddNew() {
        measures.add(new Measure("Новая единица измерения"));
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public boolean isWrite() {
        return write;
    }

    public Measure getSelectedMeasure() {
        return selectedMeasure;
    }

    public void setSelectedMeasure(Measure selectedMeasure) {
        this.selectedMeasure = selectedMeasure;
    }

    public boolean isDisableRemoveBtn() {
        return disableRemoveBtn;
    }
}
