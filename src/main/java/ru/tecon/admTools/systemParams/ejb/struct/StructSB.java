package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.struct.*;

import javax.annotation.Resource;
import javax.ejb.*;
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

    private static final String SELECT_PROP_VAL_TYPES = "select * from sys_0001t.sel_type_props_type()";
    private static final String SELECT_PROP_CAT = "select * from sys_0001t.sel_type_props_cat()";
    private static final String SELECT_SP_HEADERS = "select * from sys_0001t.sel_sp_header()";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @Resource
    private EJBContext context;

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
            cStm.setInt(1, structType.getId());
            cStm.registerOutParameter(2, Types.VARCHAR);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("remove struct " + structType.getName() + " result " + cStm.getShort(5) + " message " + cStm.getString(2));

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException(cStm.getString(2));
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
            cStm.setInt(1, structTypeID);
            cStm.setInt(2, structTypeProp.getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("remove struct property " + structTypeProp.getName() + " for struct id " + structTypeID +
                    " result " + cStm.getShort(6) + " message " + cStm.getString(3));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException(cStm.getString(3));
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

            cStm.setString(1, structType.getName());
            cStm.setString(2, structType.getTypeChar());
            cStm.registerOutParameter(3, Types.INTEGER);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("add struct " + structType.getName() + " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка добавления структуры " + structType.getName());
            }

            return cStm.getInt(3);
        } catch (SQLException e) {
            context.setRollbackOnly();
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
            cStm.setInt(1, structTypeID);
            cStm.setString(2, structTypeProp.getName());
            cStm.setString(3, structTypeProp.getType().getCode());
            cStm.setString(4, structTypeProp.getCat().getId());
            cStm.setInt(5, structTypeProp.getMeasure().getId());

            if (structTypeProp.getDef() == null) {
                cStm.setNull(6, Types.VARCHAR);
            } else {
                cStm.setString(6, structTypeProp.getDef());
            }

            if (structTypeProp.getSpHeader().getId() == null) {
                cStm.setNull(7, Types.INTEGER);
            } else {
                cStm.setInt(7, structTypeProp.getSpHeader().getId());
            }

            cStm.registerOutParameter(8, Types.INTEGER);
            cStm.registerOutParameter(9, Types.VARCHAR);
            cStm.setString(10, login);
            cStm.setString(11, ip);
            cStm.registerOutParameter(12, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("add struct property " + structTypeProp.getName() + " for struct id " + structTypeID +
                    " result " + cStm.getShort(12) + " message " + cStm.getString(9));

            if (cStm.getShort(12) != 0) {
                throw new SystemParamException(structTypeProp.getName() + ": " + cStm.getString(9));
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
                cStmUp.setInt(1, typeID);
                cStmUp.setInt(2, propID);
                cStmUp.registerOutParameter(3, Types.SMALLINT);

                for (int i = 0; i < Math.abs(direction); i++) {
                    cStmUp.executeUpdate();

                    LOGGER.info("up move return: " + cStmUp.getShort(3));
                    if (cStmUp.getShort(3) != 0) {
                        throw new SystemParamException("Ошибка перемещения свойства");
                    }
                }
            } else {
                cStmDown.setInt(1, typeID);
                cStmDown.setInt(2, propID);
                cStmDown.registerOutParameter(3, Types.SMALLINT);

                for (int i = 0; i < Math.abs(direction); i++) {
                    cStmDown.executeUpdate();

                    LOGGER.info("down move return: " + cStmDown.getShort(3));
                    if (cStmDown.getShort(3) != 0) {
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
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StructTypeProp(res.getInt("prop_id"), res.getString("prop_name"),
                        new PropValType(res.getString("prop_type"), res.getString("prop_val_type_name")),
                        new PropCat(res.getString("prop_cat"), res.getString("prop_cat_name")),
                        res.getString("prop_def"),
                        new Measure(res.getInt("prop_measure"), res.getString("measure_name"), res.getString("short_name")),
                        new SpHeader(res.getInt("sp_header_id"), res.getString("sp_header_name"))));
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
}
