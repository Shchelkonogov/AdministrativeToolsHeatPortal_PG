package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.MultiYearTempSB;
import ru.tecon.admTools.systemParams.model.MultiYearTemp;

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
 * Контроллер для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
@Named("multiYearTemp")
@ViewScoped
public class MultiYearTempMB implements Serializable, AutoUpdate {

    private List<MultiYearTemp> multiYearTemps = new ArrayList<>();

    @Inject
    private transient Logger logger;

    @EJB
    private MultiYearTempSB multiYearTempSB;

    @Inject
    private SystemParamsUtilMB utilMB;

    @Override
    public void update() {
        logger.info("load form data");

        multiYearTemps = multiYearTempSB.getMultiTnv();
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
                multiYearTempSB.updateMultiYearTemp(multiYearTemp, utilMB.getLogin(), utilMB.getIp());
                multiYearTemp.updateTemperature();
            } catch (SystemParamException e) {
                multiYearTemp.revert();
                errorMessages.add(multiYearTemp.getName());
                logger.warning(e.getMessage());
            }
        });

        update();

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

    public List<MultiYearTemp> getMultiYearTemps() {
        return multiYearTemps;
    }
}
