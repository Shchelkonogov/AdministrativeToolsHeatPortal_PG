package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.Measure;

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
 * Stateless bean для получения данных из база для формы единицы измерений
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class MeasureSB {

    private static final Logger LOGGER = Logger.getLogger(MeasureSB.class.getName());

    private static final String SELECT_MEASURE = "select * from sys_0001t.sel_measure()";
    private static final String FUN_ADD_MEAUSRE = "{? = call sys_0001t.add_measure(?, ?, ?, ?, ?)}";
    private static final String FUN_UPD_MEASURE = "{? = call sys_0001t.upd_measure(?, ?, ?, ?, ?)}";
    private static final String FUN_DEL_MEASURE = "{? = call sys_0001t.del_measure(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * @return список единиц измерений
     */
    public List<Measure> getMeasures() {
        List<Measure> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_MEASURE)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Measure(res.getInt("measure_id"),
                        res.getString("measure_name"),
                        res.getString("short_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Обновление идиницы измерения
     * @param measure единица измерений
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateMeasure(Measure measure, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_MEASURE)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, measure.getId());
            cStm.setString(3, measure.getName());
            cStm.setString(4, measure.getShortName());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления " + measure.getName());
            }

            LOGGER.info("update measure " + measure.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update measure", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Создание новой единицы измерения
     * @param measure единица зимерения
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @return id новой единицы измерения
     * @throws SystemParamException в случае ошибки создания новой единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int addMeasure(Measure measure, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_MEAUSRE)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, measure.getName());
            cStm.setString(3, measure.getShortName());
            cStm.registerOutParameter(4, Types.INTEGER);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка создания " + measure.getName());
            }

            LOGGER.info("add measure " + measure.getName() + " new id " + cStm.getInt(4));

            return cStm.getInt(4);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add measure", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление единицы измерения
     * @param measure единица измерения
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeMeasure(Measure measure, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_MEASURE)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, measure.getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                LOGGER.warning("error remove measure " + measure.getName());

                throw new SystemParamException("Ошибка удаления " + cStm.getString(3));
            }

            LOGGER.info("remove measure " + measure.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error remove measure", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
