package ru.tecon.admTools.specificModel.report.ejb;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

/**
 * Local интрерфейс для получения данных по потчету "Режимная карта"
 */
@Local
public interface ModeMapLocal {

    /**
     * Метод выгружает из бд имя объекта.
     * @param objectID id объекта
     * @return имя объекта
     */
    String getName(Integer objectID);

    /**
     * Метод выгружает из базы данные для отчета
     * @param objectID id объекта
     * @return данные
     */
    Map<String, String> loadSingleData(int objectID);

    /**
     * Метод выгружает из базы данные для отчета, данные таблиц
     * @param objectID id объекта
     * @return данные
     */
    Map<String, List<Map<String, String>>> loadArrayData(int objectID);
}
