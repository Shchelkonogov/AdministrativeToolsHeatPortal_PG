package ru.tecon.admTools.systemParams.cdi.temerature;

import ru.tecon.admTools.systemParams.ejb.temperature.TemperatureLocal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Контроллер для формы температурные графики
 * @author Maksim Shchelkonogov
 */
@Named("tempGraphs")
@ViewScoped
public class TempGraphsMB extends TemperatureMB {

    private static final String HEADER_TYPE = "Температурные графики";
    private static final String HEADER_PROP = "Значения температурного графика";
    private static final String HEADER_ADD_DIALOG = "Добавить новое значение температурного графика";

    @EJB(beanName = "tempGraphSB")
    private TemperatureLocal temperatureBean;

    @PostConstruct
    protected void init() {
        super.setTemperatureBean(temperatureBean);
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

    @Override
    public long getMinXValue() {
        return -40;
    }

    @Override
    public long getMaxXValue() {
        return 20;
    }

    @Override
    public long getMinYValue() {
        return 0;
    }

    @Override
    public long getMaxYValue() {
        return 200;
    }
}
