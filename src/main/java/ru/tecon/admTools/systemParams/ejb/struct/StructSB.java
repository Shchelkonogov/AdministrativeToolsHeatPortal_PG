package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.struct.*;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для подгрузки данных по формам Подразделения, Объекты, Агрегаты, Техпроцессы
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class StructSB {

    private static final Logger LOGGER = Logger.getLogger(StructSB.class.getName());

    private static final String SELECT_PROP_VAL_TYPES = "select * from table(ADMIN.sys_0001t.sel_type_props_type())";
    private static final String SELECT_PROP_CAT = "select * from table(sys_0001t.sel_type_props_cat())";
    private static final String SELECT_SP_HEADERS = "select * from table(sys_0001t.sel_sp_header())";
    private static final String FUN_ADD_SYS_PROP_TO_STRUCT = "{? = call sys_0001t.add_sys_prop(?, ?, ?, ?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Метод удаляет из базы выбранную структуру
     * @param structType данные по структуре
     * @param login пользователя
     * @param ip пользователя
     * @param select запрос для удаления структуры
     * @throws SystemParamException в случае если произошла ошибка удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeStruct(StructType structType, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, String.valueOf(structType.getId()));
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            LOGGER.info("remove struct " + structType.getName() + " result " + cStm.getInt(1) + " message " + cStm.getString(3));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Метод удаляет свойство структуры
     * @param structTypeID id структуры
     * @param structTypeProp данные по свойству
     * @param login пользователя
     * @param ip пользователя
     * @param select запрос для удаления свойства
     * @throws SystemParamException в случае если произошла ошибка удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, structTypeID);
            cStm.setInt(3, structTypeProp.getId());
            cStm.registerOutParameter(4, Types.VARCHAR);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("remove struct property " + structTypeProp.getName() + " for struct id " + structTypeID +
                    " result " + cStm.getInt(1) + " message " + cStm.getString(4));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(4));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Метод добавляет структуру в базу
     * @param structType данные по структуре
     * @param login пользователя
     * @param ip пользователя
     * @param select запрос на добавления структуры
     * @return id новой структуры
     * @throws SystemParamException в случае есть произошла ошибка добавления структуры
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int addStruct(StructType structType, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {

            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, structType.getName());
            cStm.setString(3, structType.getTypeChar());
            cStm.registerOutParameter(4, Types.INTEGER);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("add struct " + structType.getName() + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка добавления структуры " + structType.getName());
            }

            return cStm.getInt(4);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Метод добавляет свойство структуры
     * @param structTypeID id структуры
     * @param structTypeProp данные по свойству
     * @param login пользователя
     * @param ip пользователя
     * @param select запрос для добавления свойства
     * @throws SystemParamException в случае если произошла ошибка добавления свойства
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, structTypeID);
            cStm.setString(3, structTypeProp.getName());
            cStm.setString(4, structTypeProp.getType().getCode());
            cStm.setString(5, structTypeProp.getCat().getId());
            cStm.setInt(6, structTypeProp.getMeasure().getId());

            if (structTypeProp.getDef() == null) {
                cStm.setNull(7, Types.VARCHAR);
            } else {
                cStm.setString(7, structTypeProp.getDef());
            }

            if (structTypeProp.getSpHeader().getId() == null) {
                cStm.setNull(8, Types.INTEGER);
            } else {
                cStm.setInt(8, structTypeProp.getSpHeader().getId());
            }

            cStm.registerOutParameter(9, Types.INTEGER);
            cStm.registerOutParameter(10, Types.VARCHAR);
            cStm.setString(11, login);
            cStm.setString(12, ip);

            cStm.executeUpdate();

            LOGGER.info("add struct property " + structTypeProp.getName() + " for struct id " + structTypeID +
                    " result " + cStm.getInt(1) + " message " + cStm.getString(10));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(structTypeProp.getName() + ": " + cStm.getString(10));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Метод изменяет приоритет(положение(важность)) свойств в базе
     * @param typeID id стуктуры
     * @param propID id свойства
     * @param oldPosition индекс старой позиции
     * @param newPosition индекс новой позиции
     * @param selectUp запрос для перемещения вверх
     * @param selectDown запрос для перемещения вниз
     * @throws SystemParamException в случае если произошла ошибка перемещения свойств
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void moveProp(int typeID, int propID, int oldPosition, int newPosition, String selectUp, String selectDown) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStmUp = connect.prepareCall(selectUp);
             CallableStatement cStmDown = connect.prepareCall(selectDown)) {

            int direction = newPosition - oldPosition;

            if (direction == 0) {
                return;
            }

            if (direction < 0) {
                cStmUp.registerOutParameter(1, Types.INTEGER);
                cStmUp.setInt(2, typeID);
                cStmUp.setInt(3, propID);

                for (int i = 0; i < Math.abs(direction); i++) {
                    cStmUp.executeUpdate();

                    LOGGER.info("up move return: " + cStmUp.getInt(1));
                    if (cStmUp.getInt(1) != 0) {
                        throw new SystemParamException("Ошибка перемещения свойства");
                    }
                }
            } else {
                cStmDown.registerOutParameter(1, Types.INTEGER);
                cStmDown.setInt(2, typeID);
                cStmDown.setInt(3, propID);

                for (int i = 0; i < Math.abs(direction); i++) {
                    cStmDown.executeUpdate();

                    LOGGER.info("down move return: " + cStmDown.getInt(1));
                    if (cStmDown.getInt(1) != 0) {
                        throw new SystemParamException("Ошибка перемещения свойства");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * @param select запрос на получения структур
     * @return список структур
     */
    public List<StructType> getStructTypes(String select) {
        List<StructType> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StructType(res.getInt("type_id"),
                        res.getString("type_name"),
                        res.getString("type_char")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @param id структуры
     * @param select запрос на получение свойств структуры
     * @return список свойств для структуры
     */
    public List<StructTypeProp> getStructTypeProps(int id, String select) {
        List<StructTypeProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            stm.setInt(1, id);
            stm.setInt(2, id);
            stm.setInt(3, id);
            stm.setInt(4, id);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StructTypeProp(res.getInt("prop_id"), res.getString("prop_name"),
                        new PropValType(res.getString("prop_type"), res.getString("prop_val_type_name")),
                        new PropCat(res.getString("prop_cat"), res.getString("prop_cat_name")),
                        res.getString("prop_def"),
                        new Measure(res.getInt("prop_measure"), res.getString("measure_name"), res.getString("short_name")),
                        new SpHeader(res.getInt("sp_header_id"), res.getString("sp_header_name")), res.getInt("prop_count")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return список возможных типов для свойств структур
     */
    public List<PropValType> getPropValTypes() {
        List<PropValType> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PROP_VAL_TYPES)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new PropValType(res.getString("prop_val_type"),
                        res.getString("prop_val_type_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return список возможных категорий для свойств структуры
     */
    public List<PropCat> getPropCat() {
        List<PropCat> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PROP_CAT)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new PropCat(res.getString("prop_cat_id"),
                        res.getString("prop_cat_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return список возможных названий справочников для свойств структур
     */
    public List<SpHeader> getSpHeaders() {
        List<SpHeader> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_SP_HEADERS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new SpHeader(res.getInt("sp_header_id"),
                        res.getString("sp_header_name")));
            }

            result.add(0, new SpHeader(null, "Без справочника"));
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Добавляем системное свойство в структуру
     * @param type тип структуры (STRUCT подразделения / OBJ объекты / AGR агрегаты / TECHPROC техпроцессы / DEV устройства)
     * @param structID id структуры
     * @param sysPropID id системного свойства
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошикби записи данных в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addSystemPropToStruct(String type, int structID, int sysPropID, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_SYS_PROP_TO_STRUCT)) {

            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, type);
            cStm.setInt(3, structID);
            cStm.setInt(4, sysPropID);
            cStm.registerOutParameter(5, Types.INTEGER);
            cStm.registerOutParameter(6, Types.VARCHAR);
            cStm.setString(7, login);
            cStm.setString(8, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(6));
            }

            LOGGER.info("add system property id " + sysPropID + " to struct id " + structID);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add system property to struct", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
