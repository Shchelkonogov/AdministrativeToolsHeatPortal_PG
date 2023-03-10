package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

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
 * Stateless bean для получения данных из база для формы статистических агрегатов
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class StatAggrSB {
    private static final Logger LOGGER = Logger.getLogger(StatAggrSB.class.getName());

    private static final String SQL_SELECT_ALL_PARAMS = "select * from sys_0001t.sel_stat_agr();";
    private static final String FUN_ADD_STAT_AGGR = "call sys_0001t.add_stat_agr(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_DEL_STAT_AGGR = "call sys_0001t.del_stat_agr(?, ?, ?, ?, ?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Обновление табличных значений
     * @return наименование параметра техпроцесса и обозначение
     */
    public List<StatAggrTable> getSATabeParam() {
        List<StatAggrTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
            PreparedStatement stm = connect.prepareStatement(SQL_SELECT_ALL_PARAMS)) {

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new StatAggrTable(res.getLong("stat_agr_id"),
                        res.getString("stat_agr_code"),
                        res.getString("stat_agr_name"),
                        res.getString("dif_int")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Создание новой единицы измерения
     * @param addStatAggrTable статистический агрегат
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки создания новой единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addStatAggr(StatAggrTable addStatAggrTable, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_STAT_AGGR)) {
            cStm.setLong(1, addStatAggrTable.getId());
            cStm.setString(2, addStatAggrTable.getStat_agr_code());
            cStm.setString(3, addStatAggrTable.getStat_agr_name());
            cStm.setString(4, addStatAggrTable.getDif_int());
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка создания " + addStatAggrTable.getStat_agr_name());
            }

            LOGGER.info("add stat aggr " + addStatAggrTable.getStat_agr_name());

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add stat aggr", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление единицы измерения
     * @param statAggrTable статистический агрегат
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeParamFromTable(StatAggrTable statAggrTable, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_STAT_AGGR)) {
            cStm.setLong(1,statAggrTable.getId());
            cStm.registerOutParameter(2, Types.VARCHAR);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(5) != 0) {
                LOGGER.warning("error remove stat aggr " + statAggrTable.getStat_agr_name());

                throw new SystemParamException("Ошибка удаления " + cStm.getString(2));
            }

            LOGGER.info("remove stat aggr " + statAggrTable.getStat_agr_name());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error remove stat aggr", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
