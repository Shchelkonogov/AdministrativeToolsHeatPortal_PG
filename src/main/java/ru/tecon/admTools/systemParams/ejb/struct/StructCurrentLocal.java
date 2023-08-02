package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.struct.StructType;
import ru.tecon.admTools.systemParams.model.struct.StructTypeProp;

import javax.ejb.Local;
import java.util.List;

/**
 * Remote интерфейс для реализаций уточненных методов получения данных по группе форм структура
 * @author Maksim Shchelkonogov
 */
@Local
public interface StructCurrentLocal {

    /**
     * Метод удаляет из базы выбранную структуру
     * @param structType данные по структуре
     * @param login пользователя
     * @param ip пользователя
     * @throws SystemParamException в случае если произошла ошибка удаления
     */
    void removeStruct(StructType structType, String login, String ip) throws SystemParamException;

    /**
     * Метод удаляет свойство структуры
     * @param structTypeID id структуры
     * @param structTypeProp данные по свойству
     * @param login пользователя
     * @param ip пользователя
     * @throws SystemParamException в случае если произошла ошибка удаления
     */
    void removeStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException;

    /**
     * Метод добавляет структуру в базу
     * @param structType данные по структуре
     * @param login пользователя
     * @param ip пользователя
     * @return id новой структуры
     * @throws SystemParamException в случае есть произошла ошибка добавления структуры
     */
    int addStruct(StructType structType, String login, String ip) throws SystemParamException;

    /**
     * Метод добавляет свойство структуры
     * @param structTypeID id структуры
     * @param structTypeProp данные по свойству
     * @param login пользователя
     * @param ip пользователя
     * @throws SystemParamException в случае если произошла ошибка добавления свойства
     */
    void addStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException;

    /**
     * @return список структур
     */
    List<StructType> getStructTypes();

    /**
     * @param id структуры
     * @return список свойств для структуры
     */
    List<StructTypeProp> getStructTypeProps(int id);

    /**
     * Метод изменяет приоритет(положение(важность)) свойств в базе
     * @param typeID id стуктуры
     * @param propID id свойства
     * @param oldPosition индекс старой позиции
     * @param newPosition индекс новой позиции
     * @throws SystemParamException в случае если произошла ошибка перемещения свойств
     */
    void moveProp(int typeID, int propID, int oldPosition, int newPosition) throws SystemParamException;
}
