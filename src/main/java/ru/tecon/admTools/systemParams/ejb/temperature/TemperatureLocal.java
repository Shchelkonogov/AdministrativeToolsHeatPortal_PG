package ru.tecon.admTools.systemParams.ejb.temperature;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureProp;

import javax.ejb.Local;
import java.util.List;

/**
 * Интерфейс для загрузки данных для группы форм температур (температурные графики, суточные снижения)
 * @author Maksim Shchelkonogov
 */
@Local
public interface TemperatureLocal {

    /**
     * Получение списка типов
     * @return список типов
     */
    List<Temperature> getTemperatures();

    /**
     * Получение свойств типа
     * @param temperature тип
     * @return список свойств
     */
    List<TemperatureProp> loadTemperatureProps(Temperature temperature);

    /**
     * Метод удаляет тип
     * @param temperature тип
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки базы
     */
    void removeTemperature(Temperature temperature, String login, String ip) throws SystemParamException;

    /**
     * Метод удаляет свойство типа
     * @param id идентификатор типа
     * @param temperatureProp свойство типа
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки базы
     */
    void removeTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip) throws SystemParamException;

    /**
     * Создание нового свойства для типа
     * @param id идентификатор типа
     * @param temperatureProp новое свойство
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки базы
     */
    void addTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip) throws SystemParamException;

    /**
     * Создание нового типа
     * @param temperature новый тип
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @return новый идентификатор типа
     * @throws SystemParamException в случае ошибки базы
     */
    int createTemperature(Temperature temperature, String login, String ip) throws SystemParamException;

    /**
     * Поиск типа по его id
     * @param id идентификатор типа
     * @return найденный тип
     */
    Temperature findById(int id);
}
