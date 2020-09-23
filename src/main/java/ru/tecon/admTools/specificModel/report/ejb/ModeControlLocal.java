package ru.tecon.admTools.specificModel.report.ejb;

import ru.tecon.admTools.specificModel.report.model.ModeControlModel;

import javax.ejb.Local;
import java.util.List;

/**
 * Local интрерфейс для получения данных по потчету "Контроль режима"
 */
@Local
public interface ModeControlLocal {

    /**
     * Метод выгружает имя параметра из базы
     * @param paramID id параметра
     * @return имя
     */
    String getParamName(int paramID);

    /**
     * Метод выгружает из бд путь организационной структуры к структуре
     * @param structID id структуры
     * @return путь
     */
    String getStructPath(int structID);

    /**
     * Метод выгружает из базы данные для отчета "Контроль режима"
     * @param objType тип объекта
     * @param structID id структуры
     * @param filterType id фильтра
     * @param filter текст фильтра
     * @param paramID id параметра
     * @param user имя пользователя
     * @return список данных для отчета {@link ModeControlModel}
     */
    List<ModeControlModel> getData(int objType, int structID, int filterType, String filter, int paramID, String user);
}
