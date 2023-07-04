package ru.tecon.admTools.systemParams.ejb.temperature;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureProp;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Stateless bean для получения данных по форме суточные снижения
 * @author Maksim Shchelkonogov
 */
@Stateless(name = "dailyReductionSB")
@Local(TemperatureLocal.class)
public class DailyReductionSB implements TemperatureLocal {

    private static final String SEL_TEMP_GRAPHS = "select * from dsp_0031t.sel_decrease_list()";
    private static final String SEL_TEMP_GRAPH_PROPS = "select * from dsp_0031t.sel_decrease_value(?)";
    private static final String FUN_REMOVE_TEMP_GRAPH = "call sys_0001t.del_decrease(?, ?, ?, ?, ?)";
    private static final String FUN_REMOVE_TEMP_GRAPH_PROP = "call sys_0001t.del_decrease_value(?, ?, ?, ?, ?, ?)";
    private static final String FUN_CREATE_TEMP_GRAPH = "call sys_0001t.add_decrease(?, ?, ?, ?, ?, ?)";
    private static final String FUN_CREATE_TEMP_GRAPH_PROP = "call sys_0001t.add_decrease_value(?, ?, ?, ?, ?, ?)";
    private static final String FIND_TEMP_BY_ID = "select * from dsp_0031t.sel_decrease_list() where graph_id = ?";

    @EJB
    private TemperatureSB wrapperTemperatureBean;

    @Override
    public List<Temperature> getTemperatures() {
        return wrapperTemperatureBean.getTemperatures(SEL_TEMP_GRAPHS);
    }

    @Override
    public List<TemperatureProp> loadTemperatureProps(Temperature temperature) {
        return wrapperTemperatureBean.loadTemperatureProps(temperature, SEL_TEMP_GRAPH_PROPS);
    }

    @Override
    public void removeTemperature(Temperature temperature, String login, String ip) throws SystemParamException {
        wrapperTemperatureBean.removeTemperature(temperature, login, ip, FUN_REMOVE_TEMP_GRAPH);
    }

    @Override
    public void removeTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip) throws SystemParamException {
        wrapperTemperatureBean.removeTemperatureProp(id, temperatureProp, login, ip, FUN_REMOVE_TEMP_GRAPH_PROP);
    }

    @Override
    public void addTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip) throws SystemParamException {
        wrapperTemperatureBean.addTemperatureProp(id, temperatureProp, login, ip, FUN_CREATE_TEMP_GRAPH_PROP);
    }

    @Override
    public int createTemperature(Temperature temperature, String login, String ip) throws SystemParamException {
        return wrapperTemperatureBean.createTemperature(temperature, login, ip, FUN_CREATE_TEMP_GRAPH);
    }

    @Override
    public Temperature findById(int id) {
        return wrapperTemperatureBean.getTemperatureById(id, FIND_TEMP_BY_ID);
    }
}
