package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.groundTemp.GroundTemp;
import ru.tecon.admTools.systemParams.model.groundTemp.ReferenceValue;

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
 * Stateless bean для работы с формой температура грунта
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class GroundTempSB {

    private static final Logger LOGGER = Logger.getLogger(GroundTempSB.class.getName());

    private static final String SEL_GROUND_TEMPS = "select time_stamp, value from sys_0001t.sel_ground_temp()";
    private static final String FUN_ADD_GROUND_TEMP = "call sys_0001t.add_ground_temp(?, ?, ?, ?, ?)";

    private static final String SEL_REFERENCE_VALUES = "select * from sys_0001t.sel_norm_ind_te()";
    private static final String FUN_UPD_REFERENCE_VALUE = "call sys_0001t.upd_norm_ind_te(?, ?, ?, ?, ?, ?, ?, ?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Метод для получения всех температур грунта из базы
     * @return список температур грунта
     */
    public List<GroundTemp> getGroundTemps() {
        List<GroundTemp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_GROUND_TEMPS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new GroundTemp(res.getDate("time_stamp").toLocalDate(), res.getDouble("value")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load ground temps", e);
        }
        return result;
    }

    /**
     * Метод для добавления новой температуры грунта
     * @param groundTemp температура грунта
     * @param login имя пользователя
     * @param ip адрес
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addGroundTemp(GroundTemp groundTemp, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_GROUND_TEMP)) {
            cStm.setObject(1, Timestamp.valueOf(groundTemp.getDate().atStartOfDay()), Types.TIMESTAMP);
            cStm.setObject(2, groundTemp.getValue(), Types.NUMERIC);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Невозможно добавить температуру грунта");
            }

            LOGGER.info("add ground temp " + groundTemp);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add ground temp", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Метод для получения эталонных значений температур
     * @return список эталонных значений температур
     */
    public List<ReferenceValue> getReferenceValues() {
        List<ReferenceValue> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_REFERENCE_VALUES)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ReferenceValue(res.getDouble("t3_e"), res.getDouble("t4_e"),
                        res.getDouble("t7_e"), res.getDouble("t13_e"), res.getDouble("tgr_e")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load reference values", e);
        }
        return result;
    }

    /**
     * Метод для записи в базу изменений в эталонных значений температур
     * @param referenceValue измененные эталонные значения температур
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateReferenceValue(ReferenceValue referenceValue, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_REFERENCE_VALUE)) {
            cStm.setObject(1, referenceValue.getT3(), Types.NUMERIC);
            cStm.setObject(2, referenceValue.getT4(), Types.NUMERIC);
            cStm.setObject(3, referenceValue.getT7(), Types.NUMERIC);
            cStm.setObject(4, referenceValue.getT13(), Types.NUMERIC);
            cStm.setObject(5, referenceValue.getTgr(), Types.NUMERIC);
            cStm.setString(6, login);
            cStm.setString(7, ip);
            cStm.registerOutParameter(8, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(8) != 0) {
                throw new SystemParamException("Невозможно обновить эталонные значения температуры");
            }

            LOGGER.info("update reference temp " + referenceValue);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add ground temp", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
