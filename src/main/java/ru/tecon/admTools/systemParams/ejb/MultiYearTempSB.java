package ru.tecon.admTools.systemParams.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.MultiYearTemp;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для обработки запросов в базу для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class MultiYearTempSB {

    private static final Logger LOGGER = Logger.getLogger(MultiYearTempSB.class.getName());

    private static final String SEL_TNV = "select * from sys_0001t.sel_tnvsm(?)";
    private static final String FUN_UPD_TNV = "call sys_0001t.upd_tnvsm(?, ?, ?, ?, ?, ?)";

    private static final String SEL_BOOST = "select * from sys_0001t.sel_plan_boost(?)";
    private static final String FUN_UPD_BOOST = "call sys_0001t.upd_plan_boost(?, ?, ?, ?, ?);";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получения списка температур
     * @return список данных
     */
    public List<MultiYearTemp> getMultiTnv(int year) {
        List<MultiYearTemp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TNV)) {
            stm.setInt(1, year);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new MultiYearTemp(res.getInt("month_num"), res.getString("month_name"), res.getDouble("tnvsm")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load multi tnv data", e);
        }

        return result;
    }

    /**
     * Запись обновленного списка температур
     *
     * @param temp обновленные данные
     * @param year год
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateMultiYearTemp(MultiYearTemp temp, int year, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_TNV)) {
            cStm.setInt(1, year);
            cStm.setInt(2, temp.getId());
            cStm.setObject(3, temp.getValue(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка обновления " + temp.getName());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update multi year temp", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получения планового увеличения нагрузки
     *
     * @return плановое увеличение нагрузки
     */
    public double getBoostValue(int year) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_BOOST)) {
            stm.setInt(1, year);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load boost value", e);
        }

        return 0;
    }

    /**
     * Запись планового увеличения нагрузки
     *
     * @param year год
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBoostValue(int year, double boostValue, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_BOOST)) {
            cStm.setInt(1, year);
            cStm.setObject(2, boostValue, Types.NUMERIC);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Ошибка обновления планового увеличения нагрузки");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update boost value", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
