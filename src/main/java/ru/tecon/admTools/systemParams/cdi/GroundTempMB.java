package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.GroundTempSB;
import ru.tecon.admTools.systemParams.model.groundTemp.GroundTemp;
import ru.tecon.admTools.systemParams.model.groundTemp.ReferenceValue;

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
    private List<ReferenceValue> referenceValues = new ArrayList<>();

    private String login;
    private String ip;
    private boolean write = false;

    @EJB
    private GroundTempSB groundTempSB;

    @PostConstruct
    private void init() {
        groundTemps = groundTempSB.getGroundTemps();
        referenceValues = groundTempSB.getReferenceValues();

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
     * Обрабочик изменения значения эталонной температуры (изменение цвета ячейки)
     * @param event событие изменения значения
     */
    public void onCellEdit(CellEditEvent event) {
        String clientID = event.getColumn().getChildren().get(0).getClientId().replaceAll(":", "\\:");
        PrimeFaces.current().executeScript("document.getElementById('" + clientID + "').style.backgroundColor = 'lightgrey'");
    }

    /**
     * Обработчик кнопки "сохранить", для записи изменений
     * эталонных значений температуры в базу
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        referenceValues.stream().filter(ReferenceValue::isChange).forEach(referenceValue -> {
            LOGGER.info("update reference value " + referenceValue);

            try {
                groundTempSB.updateReferenceValue(referenceValue, login, ip);
            } catch (SystemParamException e) {
                errorMessages.add(e.getMessage());
                LOGGER.warning(e.getMessage());
            }
        });

        referenceValues = groundTempSB.getReferenceValues();

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
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

    public List<ReferenceValue> getReferenceValues() {
        return referenceValues;
    }
}
