package ru.tecon.admTools.specificModel.ejb;

import ru.tecon.admTools.specificModel.model.*;
import ru.tecon.admTools.specificModel.model.additionalModel.EnumerateData;
import ru.tecon.admTools.systemParams.SystemParamException;

import javax.ejb.Local;
import java.util.List;

/**
 * Локальный интерфейс для обработки данных для формы конкретная модель
 */
@Local
public interface SpecificModelLocal {

    /**
     * Метод выгружает данные для таблицы аналоговых данных
     * @param objectID ID объекта
     * @return список данных {@link DataModel}
     */
    List<DataModel> getData(int objectID);

    /**
     * Метод выгружает данные для таблицы аналоговых данных
     * @param objectID ID объекта
     * @param eco идентификатор показывающий является ли запрос по экомониторингу
     * @return список данных {@link DataModel}
     */
    List<DataModel> getData(int objectID, boolean eco);

    /**
     * Метод выгружает строку описания объекта (организационный путь и территориальный адрес)
     * @param objectID id объекта
     * @return строка описания объекта
     */
    String getObjectPath(int objectID);

    /**
     * Метод выбружает данные для таблицы перечислимых данных
     * @param objectID id объекта
     * @return список данных {@link DataModel}
     */
    List<DataModel> getEnumerableData(int objectID);

    /**
     * Метод выгружает дополнительные данные для перечислимых данных
     * @param objectID id объекта
     * @param parId id параметра
     * @param statAgrID id агрегата параметра
     * @return дополнительные данные {@link EnumerateData}
     */
    EnumerateData getParamCondition(int objectID, int parId, int statAgrID);

    /**
     * Метод выгружает список возможных состояния перечислимых параметров
     * @return список состояний {@link Condition}
     */
    List<Condition> getConditions();

    /**
     * Метод выгружает списко возможных графиков для аналоговых данных
     * @return список графиков {@link GraphDecreaseItemModel}
     */
    List<GraphDecreaseItemModel> getDecreases();

    /**
     * Метод выгружает списко возможных снижений для аналоговых данных
     * @return список снижений {@link GraphDecreaseItemModel}
     */
    List<GraphDecreaseItemModel> getGraphs();

    /**
     * Метод сохраняет изменения дополнительных данных для перечеслимых объектов
     * @param objectID id объекта
     * @param saveData данные для сохранения
     */
    void saveEnumParam(int objectID, DataModel saveData, String login) throws SystemParamException;


    /**
     * Метод сохраняет изменения для аналоговых параметров
     * @param objectID id объекта
     * @param saveData данные для сохранения
     * @throws SystemParamException в случае ошибки сохранения возвращается сообщение об ошибке.
     */
    void saveAParam(int objectID, DataModel saveData, String login, boolean eco) throws SystemParamException;

    /**
     * Метод выгружает данные по описанию графика
     * @param graphID id графика
     * @return список данных {@link GraphDecreaseDescription}
     */
    List<GraphDecreaseDescription> getGraphDescription(int graphID);

    /**
     * Метод выгружает данные по описанию суточному снижению
     * @param decreaseID id суточного снижения
     * @return список данных {@link GraphDecreaseDescription}
     */
    List<GraphDecreaseDescription> getDecreaseDescription(int decreaseID);

    /**
     * Метод выгружает историю изменений парамтеров
     * @param objectID id объекта
     * @param paramID id параметра
     * @param statAgrID id агрегата
     * @return список данных {@link ParamHistory}
     */
    List<ParamHistory> getParamHistory(int objectID, int paramID, int statAgrID);

    /**
     * Метод сбрасывает границы у выбранного параметра
     * @param objectID id объекта
     * @param parID id параметра
     * @param statAgrID id агрегата
     * @param user идентификатор пользователя, от которого производится сброс границ
     */
    void clearRanges (int objectID, int parID, int statAgrID, boolean eco, String user) throws SystemParamException;
}
