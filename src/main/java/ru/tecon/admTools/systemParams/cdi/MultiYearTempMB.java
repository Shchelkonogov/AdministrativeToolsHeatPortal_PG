package ru.tecon.admTools.systemParams.cdi;

import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MultiYearTempSB;
import ru.tecon.admTools.systemParams.model.MultiYearTemp;

import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Контроллер для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
@Named("multiYearTemp")
@ViewScoped
public class MultiYearTempMB implements Serializable, AutoUpdate {

    private List<MultiYearTemp> multiYearTemps = new ArrayList<>();
    private int year;
    private double boostValue;

    @Inject
    private transient Logger logger;

    @EJB
    private MultiYearTempSB multiYearTempSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        logger.info("load form data");

        year = Year.now().getValue();
        load();
    }

    private void load() {
        multiYearTemps = multiYearTempSB.getMultiTnv(year);
        boostValue = multiYearTempSB.getBoostValue(year);
    }

    /**
     * Метод обрабатывает нажатие на кнопку сохранить.
     * Сохраняет изменения температур в базу
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        multiYearTemps.stream().filter(MultiYearTemp::isChanged).forEach(multiYearTemp -> {
            logger.info("update for login " + utilMB.getLogin() + " and ip " + utilMB.getIp() + " temperature " + multiYearTemp);

            try {
                multiYearTempSB.updateMultiYearTemp(multiYearTemp, year, utilMB.getLogin(), utilMB.getIp());
                multiYearTemp.updateTemperature();
            } catch (SystemParamException e) {
                multiYearTemp.revert();
                errorMessages.add(multiYearTemp.getName());
                logger.warning(e.getMessage());
            }
        });

        try {
            multiYearTempSB.updateBoostValue(year, boostValue, utilMB.getLogin(), utilMB.getIp());
        } catch (SystemParamException e) {
            errorMessages.add(e.getMessage());
            logger.warning(e.getMessage());
        }

        load();

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    /**
     * Обработчик изменения значения температуры (изменение цвета ячейки)
     * @param event событие изменения значения
     */
    public void onCellEdit(CellEditEvent<?> event) {
        String clientID = event.getColumn().getChildren().get(0).getClientId().replaceAll(":", "\\:");
        PrimeFaces.current().executeScript("document.getElementById('" + clientID + "').parentNode.style.backgroundColor = 'lightgrey'");
    }

    /**
     * Обработчик выбора года
     *
     * @param event событие изменения годы
     */
    public void handleSpinnerValueChange(final AjaxBehaviorEvent event) {
        load();
    }

    /**
     * Обработчик изменения планового увеличения нагрузки
     *
     * @param event событие изменения планового увеличения нагрузки
     */
    public void handleBoostValueChange(final AjaxBehaviorEvent event) {
        PrimeFaces.current().executeScript("document.getElementById('multiYearTemp:boostValue_input').style.backgroundColor = 'lightgrey'");
    }

    public List<MultiYearTemp> getMultiYearTemps() {
        return multiYearTemps;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getBoostValue() {
        return boostValue;
    }

    public void setBoostValue(double boostValue) {
        this.boostValue = boostValue;
    }
}
