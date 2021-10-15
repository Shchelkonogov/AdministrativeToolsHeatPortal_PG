package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.GroundTemp;

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

    private static final String SEL_GROUND_TEMPS = "select time_stamp, value from table(sys_0001t.sel_ground_temp())";
    private static final String FUN_ADD_GROUND_TEMP = "{? = call sys_0001t.add_ground_temp(?, ?, ?, ?)}";

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
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDate(2, java.sql.Date.valueOf(groundTemp.getDate()));
            cStm.setDouble(3, groundTemp.getValue());
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Невозможно добавить температуру грунта");
            }

            LOGGER.info("add ground temp " + groundTemp);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add ground temp", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
