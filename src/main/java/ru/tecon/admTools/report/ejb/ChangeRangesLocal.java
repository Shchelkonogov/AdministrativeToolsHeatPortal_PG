package ru.tecon.admTools.report.ejb;

import ru.tecon.admTools.report.model.ChangeRangesModel;

import javax.ejb.Local;
import java.util.List;

/**
 * Local интерфейс для выгрузки данных для отчета "Отчет по изменению тех границ"
 */
@Local
public interface ChangeRangesLocal {

    /**
     * Метод выгружает из бд путь организационной структуры к объекту или структуре.
     * Если structID равен null то выгружается путь до объекта.
     * Если objectID равен null то выгружается путь до структуры.
     * @param structID id структуры
     * @param objectID id объекта
     * @return строка пути
     */
    String getPath(Integer structID, Integer objectID);

    /**
     * Выгружает данные по оптчету из базы.<br/>
     * structID или objID должен быть null, иначе данные будут пустыми.
     * @param objType тип объекта
     * @param structID id структуры
     * @param objID id объекта
     * @param filterType тип фильтрв
     * @param filter текст фильтра
     * @param date дата в формате dd.MM.yyyy
     * @param user имя пользователя
     * @return возвращает список данных по отчету {@link ChangeRangesModel}
     */
    List<ChangeRangesModel> loadReportData(int objType, Integer structID, Integer objID, int filterType,
                                           String filter, String date, String user);
}
