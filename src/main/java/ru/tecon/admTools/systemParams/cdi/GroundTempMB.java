package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.GroundTempSB;
import ru.tecon.admTools.systemParams.model.groundTemp.GroundTemp;
import ru.tecon.admTools.systemParams.model.groundTemp.ReferenceValue;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
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
public class GroundTempMB implements Serializable, AutoUpdate {

    private List<GroundTemp> groundTemps = new ArrayList<>();
    private List<ReferenceValue> referenceValues = new ArrayList<>();

    @Inject
    private transient Logger logger;

    @EJB
    private GroundTempSB groundTempSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        groundTemps = groundTempSB.getGroundTemps();
        referenceValues = groundTempSB.getReferenceValues();

        PrimeFaces.current().executeScript("PF('groundTempTableWidget').filter();");
    }

    /**
     * Обработчик сохранения изменения строки
     * @param event событие
     */
    public void onRowEdit(RowEditEvent<GroundTemp> event) {
        logger.info("update row " + event.getObject());

        GroundTemp groundTemp = event.getObject();

        try {
            if (groundTemp.getDate() == null) {
                throw new SystemParamException("Дата не может быть пустой");
            }

            groundTempSB.addGroundTemp(groundTemp, utilMB.getLogin(), utilMB.getIp());
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
    public void onCellEdit(CellEditEvent<?> event) {
        String clientID = event.getColumn().getChildren().get(0).getClientId().replaceAll(":", "\\:");
        PrimeFaces.current().executeScript("document.getElementById('" + clientID + "').parentNode.style.backgroundColor = 'lightgrey'");
    }

    /**
     * Обработчик кнопки "сохранить", для записи изменений
     * эталонных значений температуры в базу
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        referenceValues.stream().filter(ReferenceValue::isChange).forEach(referenceValue -> {
            logger.info("update reference value " + referenceValue);

            try {
                groundTempSB.updateReferenceValue(referenceValue, utilMB.getLogin(), utilMB.getIp());
            } catch (SystemParamException e) {
                errorMessages.add(e.getMessage());
                logger.warning(e.getMessage());
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

    public void onRowEditCancel(RowEditEvent<GroundTemp> event) {
        groundTemps.remove(event.getObject());
    }

    public List<GroundTemp> getGroundTemps() {
        return groundTemps;
    }

    public List<ReferenceValue> getReferenceValues() {
        return referenceValues;
    }
}
