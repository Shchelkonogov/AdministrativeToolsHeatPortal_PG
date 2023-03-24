package ru.tecon.admTools.systemParams.cdi;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.CoefficientForRegimeCardSB;
import ru.tecon.admTools.systemParams.model.CoefficientRC;

import javax.annotation.PostConstruct;
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
 * Контроллер для формы коеффициенты для режимной карты
 * @author Maksim Shchelkonogov
 */
@Named("coefficientsRC")
@ViewScoped
public class CoefficientsForRegimeCardMB implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CoefficientsForRegimeCardMB.class.getName());

    private List<CoefficientRC> coefficientRCList = new ArrayList<>();

    @EJB
    private CoefficientForRegimeCardSB coefficientBean;

    @Inject
    private SystemParamsUtilMB utilMB;

    @PostConstruct
    private void init() {
        loadData();
    }

    public void onFormLoad() {
        LOGGER.info("load form data");

        loadData();
    }

    private void loadData() {
        coefficientRCList = coefficientBean.getCoefficients();
    }

    /**
     * Обработчик нажатия кнопки сохранить
     */
    public void onSaveChanges() {
        FacesContext context = FacesContext.getCurrentInstance();

        List<String> errorMessages = new ArrayList<>();

        coefficientRCList.stream().filter(CoefficientRC::isChange).forEach(coefficientRC -> {
            LOGGER.info("update regime card coefficient " + coefficientRC);

            try {
                coefficientBean.updateCoefficient(coefficientRC, utilMB.getLogin(), utilMB.getIp());
            } catch (SystemParamException e) {
                errorMessages.add(coefficientRC.getName());
                LOGGER.warning(e.getMessage());
            }
        });

        loadData();

        if (!errorMessages.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка записи", String.join(", ", errorMessages)));
        }
    }

    public List<CoefficientRC> getCoefficientRCList() {
        return coefficientRCList;
    }

    /**
     * В случае изменения checkbox в таблице
     * @param data данные, которые изменили
     * @param columnID идентификатор колонки, которую изменили
     */
    public void onChange(CoefficientRC data, String columnID) {
        data.setChange(true);
        int index = coefficientRCList.indexOf(data);
        PrimeFaces.current().executeScript("document.getElementById('regimeCardForm\\:regimeCardTable\\:" + index +
                "\\:" + columnID + "').style.backgroundColor = 'lightgrey'");
    }

    /**
     * Обработик изменения ячейки таблицы
     * @param event событие изменения
     */
    public void onCellEdit(CellEditEvent event) {
        String clientID = event.getColumn().getChildren().get(0).getClientId().replaceAll(":", "\\:");
        PrimeFaces.current().executeScript("document.getElementById('" + clientID + "').style.backgroundColor = 'lightgrey'");
    }
}
