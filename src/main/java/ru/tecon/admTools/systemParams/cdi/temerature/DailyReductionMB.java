package ru.tecon.admTools.systemParams.cdi.temerature;

import ru.tecon.admTools.systemParams.ejb.temperature.TemperatureLocal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Контроллер для формы сыточные снижения
 * @author Maksim Shchelkonogov
 */
@Named("dailyReduction")
@ViewScoped
public class DailyReductionMB extends TemperatureMB {

    private static final String HEADER_TYPE = "Суточные снижения";
    private static final String HEADER_PROP = "Значения суточного снижения";
    private static final String HEADER_ADD_DIALOG = "Добавить новое значение суточного снижения";

    @EJB(beanName = "dailyReductionSB")
    private TemperatureLocal temperatureBean;

    @PostConstruct
    protected void init() {
        super.setTemperatureBean(temperatureBean);
        super.init();
    }

    @Override
    public String getHeaderType() {
        return HEADER_TYPE;
    }

    @Override
    public String getHeaderProp() {
        return HEADER_PROP;
    }

    @Override
    public String getHeaderAddDialog() {
        return HEADER_ADD_DIALOG;
    }
}
