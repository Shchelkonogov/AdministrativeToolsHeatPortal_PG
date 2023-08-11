package ru.tecon.admTools.linker.ejb;

import ru.tecon.admTools.linker.model.*;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.utils.AdmTools;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для получения данных для формы "Линковщик"
 *
 * @author Maksim Shchelkonogov
 * 10.07.2023
 */
@Stateless
@LocalBean
public class LinkerStateless {

    private final static String SELECT_LINKED_OBJECTS_DATA = "select * from lnk_0001t.sel_linked_object(?, '', '', cast(0 as smallint), ?);";
    private final static String SELECT_RECOUNT_DATA = "select * from lnk_0001t.sel_obj_recalc_mode(?, ?, ?);";
    private final static String PROCEDURE_RECOUNT_PRESSURE = "call lnk_0001t.recalc_pressure(?, ?, ?, ?);";
    private final static String PROCEDURE_RECOUNT_TIME = "call lnk_0001t.recalc_time(?, ?, ?, ?);";
    private final static String PROCEDURE_RECOUNT_RESISTANCE = "call lnk_0001t.recalc_resistance(?, ?, ?, ?);";
    private final static String PROCEDURE_SUBSCRIBE = "call lnk_0001t.set_subscribe(?, ?, ?);";
    private final static String PROCEDURE_CHANGE_SCHEMA_NAME = "call lnk_0001t.create_schema(?, ?);";
    private final static String PROCEDURE_REMOVE_LINK = "call lnk_0001t.unlink_opc_object(?, ?, ?, ?)";
    private final static String PROCEDURE_REMOVE_ALL_LINKS = "call lnk_0001t.unlink_aspid_object(?, ?, ?)";
    private final static String SELECT_OPC_OBJECTS_FOR_LINK = "select * from lnk_0001t.sel_unlinked_opc_object('')";
    private final static String PROCEDURE_ADD_LINK = "call lnk_0001t.link_add_opc_object(?, ?, ?, ?, ?)";
    private final static String SELECT_FULL_NAME = "select admin.get_obj_path_all(?,', ') || ' (' || admin.get_obj_address(?) || ')'";
    private final static String SELECT_CALC_TREE = "select * from lnk_0001t.sel_param_tree_calc(?);";
    private final static String SELECT_CALC_PARAM = "select par_memo, par_name, stat_agr_name, color from lnk_0001t.sel_calc_param(?, ?, ?);";
    private final static String PROCEDURE_UNLINK_CALC_PARAM = "call lnk_0001t.unlink_calc_param(?, ?, ?);";
    private final static String PROCEDURE_LINK_CALC_PARAM = "call lnk_0001t.link_calc_param(?, ?, ?);";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Загрузка линкованных данных, закладка "Линкованные объекты / Объекты"
     *
     * @param objectType тип объекта
     * @param user пользователь
     * @return коллекция данных
     */
    public List<LinkedData> getLinkedData(int objectType, String user) {
        List<LinkedData> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_LINKED_OBJECTS_DATA)) {
            stm.setInt(1, objectType);
            stm.setString(2, user);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new LinkedData(
                        new SystemObject(res.getInt("obj_id"), res.getString("obj_name")),
                        new Schema(res.getInt("schema_id"), res.getString("schema_name")),
                        new SystemObject(res.getInt("id"), res.getString("item_name")),
                        res.getString("server_name"),
                        res.getInt("subscribed") == 1,
                        res.getInt("obj_int_key")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load linked object data", ex);
        }
        return result;
    }

    /**
     * Получение информации по пересчету для переданного объекта, информация записывается в этот объект
     *
     * @param linkedData объект для получения информации по перерасчету.
     */
    public void loadRecountData(LinkedData linkedData) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_RECOUNT_DATA)) {
            stm.setInt(1, linkedData.getDbObject().getId());
            stm.setInt(2, linkedData.getObjIntKey());
            stm.setString(3, linkedData.getOpcObject().getName());

            ResultSet res = stm.executeQuery();
            if (res.next()) {
                linkedData.getRecount().put(RecountTypes.PRESSURE.name(), res.getInt("pressure_recalc_mode") == 1);
                linkedData.getRecount().put(RecountTypes.RESISTANCE.name(), res.getInt("resistance_recalc_mode") == 1);
                linkedData.getRecount().put(RecountTypes.TIME.name(), res.getInt("time_recalc_mode") == 1);
            }

            logger.log(Level.INFO, "Loaded recount data for object {0}", linkedData);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load recount data", ex);
        }
    }

    /**
     * Изменить перерасчет на объекте.
     *
     * @param linkedData объект для изменения перерасчета
     * @param type тип перерасчета
     * @return в случае удачи, передается успешное сообщение
     * @throws SystemParamException ошибка выполнения обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String recount(LinkedData linkedData, RecountTypes type) throws SystemParamException {
        try (Connection connect = ds.getConnection()) {
            String select;

            switch (type) {
                case TIME:
                    select = PROCEDURE_RECOUNT_TIME;
                    break;
                case PRESSURE:
                    select = PROCEDURE_RECOUNT_PRESSURE;
                    break;
                case RESISTANCE:
                    select = PROCEDURE_RECOUNT_RESISTANCE;
                    break;
                default:
                    throw new SystemParamException("Неизвестный тип пересчета");
            }

            try (CallableStatement cStm = connect.prepareCall(select)) {
                cStm.setInt(1, linkedData.getDbObject().getId());
                cStm.setInt(2, linkedData.getObjIntKey());
                cStm.setString(3, linkedData.getOpcObject().getName());
                cStm.registerOutParameter(4, Types.VARCHAR);

                cStm.executeUpdate();

                return cStm.getString(4);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error change recount", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Изменения подписи для объекта
     *
     * @param linkedData объект для изменения подписи
     * @throws SystemParamException ошибка выполнения обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void subscribe(LinkedData linkedData) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(PROCEDURE_SUBSCRIBE)) {

            cStm.setInt(1, linkedData.getDbObject().getId());
            cStm.setInt(2, linkedData.getObjIntKey());
            cStm.setShort(3, (short) (linkedData.isSubscribed() ? 1 : 0));

            cStm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error change subscribe", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Изменения схемы линковки для объекта
     *
     * @param linkedData объект для изменения схемы линковки
     * @throws SystemParamException ошибка выполнения обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeSchemaName(LinkedData linkedData) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(PROCEDURE_CHANGE_SCHEMA_NAME)) {

            cStm.setString(1, linkedData.getSchema().getName());
            cStm.setInt(2, linkedData.getDbObject().getId());

            cStm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error change schema name", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Удаления линквоки
     *
     * @param linkedData объект для разрыва связи
     * @param login пользователь
     * @param ip ip с которого обратился пользователь
     * @throws SystemParamException ошибка выполнения удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeLink(LinkedData linkedData, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(PROCEDURE_REMOVE_LINK)) {

            cStm.setInt(1, linkedData.getDbObject().getId());
            cStm.setInt(2, linkedData.getObjIntKey());
            cStm.setString(3, login);
            cStm.setString(4, ip);

            cStm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error remove link", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Удаления всех линковок
     *
     * @param linkedData объект для разрыва всех связей
     * @param login пользователь
     * @param ip ip с которого обратился пользователь
     * @throws SystemParamException ошибка выполнения удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeAllLinks(LinkedData linkedData, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(PROCEDURE_REMOVE_ALL_LINKS)) {

            cStm.setInt(1, linkedData.getDbObject().getId());
            cStm.setString(2, login);
            cStm.setString(3, ip);

            cStm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error remove link", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Получение списка объектов для долинковки
     *
     * @return коллекция объектов для долинковки
     */
    public List<OpcObjectForLinkData> getOpcObjectsForLink() {
        List<OpcObjectForLinkData> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_OPC_OBJECTS_FOR_LINK)) {

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new OpcObjectForLinkData(
                        new SystemObject(res.getInt("id"), res.getString("item_name")),
                        res.getInt("param_cnt"),
                        res.getInt("obj_int_key")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load opc objects for link", ex);
        }
        return result;
    }

    /**
     * Добавления связи для объекта системы
     *
     * @param linkedData объект системы к которому надо добавить связь
     * @param newLinkData объект автоматизации который надо добавить
     * @param login пользователь
     * @param ip ip с которого обратился пользователь
     * @throws SystemParamException ошибка выполнения обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addLink(LinkedData linkedData, OpcObjectForLinkData newLinkData, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(PROCEDURE_ADD_LINK)) {

            cStm.setInt(1, linkedData.getDbObject().getId());
            cStm.setInt(2, newLinkData.getOpcObject().getId());
            cStm.setInt(3, newLinkData.getObjIntKey());
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error add link", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Получение полного имени объекта
     * @param id id объекта
     * @return имя объекта
     */
    public String getFullObjectName(int id) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_FULL_NAME)) {

            stm.setInt(1, id);
            stm.setInt(2, id);

            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getString(1);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load opc objects for link", ex);
        }
        return "";
    }

    /**
     * Получение данных дерева в закладке "Линкованные объекты / Вычислимые параметры"
     * @param id id выбранного объекта
     * @return данные дерева
     */
    public List<TreeData> getCalcTreeData(int id) {
        List<TreeData> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CALC_TREE)) {

            stm.setInt(1, id);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new TreeData(res.getString("id"), res.getString("name"), res.getString("parent"),
                        res.getString("my_id"), res.getString("my_type")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load calc tree data", ex);
        }
        return result;
    }

    /**
     * Получение данных для таблицы в закладке "Линкованные объекты / Вычислимые параметры"
     * @param objId id объекта
     * @param paramId id параметра
     * @param statAgrId id статистического агрегата параметра
     * @return данные для таблицы
     */
    public List<CalcDataTable> getCalcParamTable(int objId, int paramId, int statAgrId) {
        List<CalcDataTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CALC_PARAM)) {

            stm.setInt(1, objId);
            stm.setInt(2, paramId);
            stm.setInt(3, statAgrId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new CalcDataTable(res.getString("par_memo"), res.getString("stat_agr_name"),
                        res.getString("par_name"), res.getInt("color")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load calc param table", ex);
        }
        return result;
    }

    /**
     * Создает связь для параметра в закладке "Линкованные объекты / Вычислимые параметры"
     * @param objectId id объекта
     * @param paramId id параметра
     * @param statAgrId id статистического агрегата
     * @throws SystemParamException ошибка создания связи
     */
    public void linkCalcParam(int objectId, int paramId, int statAgrId) throws SystemParamException {
        changeParamLink(true, objectId, paramId, statAgrId);
    }

    /**
     * Удаляет связь для параметра в закладке "Линкованные объекты / Вычислимые параметры"
     * @param objectId id объекта
     * @param paramId id параметра
     * @param statAgrId id статистического агрегата
     * @throws SystemParamException ошибка удаления связи
     */
    public void unlinkCalcParam(int objectId, int paramId, int statAgrId) throws SystemParamException {
        changeParamLink(false, objectId, paramId, statAgrId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void changeParamLink(boolean link, int objectId, int paramId, int statAgrId) throws SystemParamException {
        try (Connection connect = ds.getConnection()) {

            String select;
            if (link) {
                select = PROCEDURE_LINK_CALC_PARAM;
            } else {
                select = PROCEDURE_UNLINK_CALC_PARAM;
            }

            try (CallableStatement cStm = connect.prepareCall(select)) {
                cStm.setInt(1, objectId);
                cStm.setInt(2, paramId);
                cStm.setInt(3, statAgrId);

                cStm.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error change param link", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }
}
