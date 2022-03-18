package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.MultiYearTemp;

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
 * Stateless bean для обработки запросов в базу для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class MultiYearTempSB {

    private static final Logger LOGGER = Logger.getLogger(MultiYearTempSB.class.getName());

    private static final String SEL_TNV = "select * from table(sys_0001t.sel_tnvsm)";
    private static final String FUN_UPD_TNV = "{? = call sys_0001t.upd_tnvsm(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получения списка температур
     * @return список данных
     */
    public List<MultiYearTemp> getMultiTnv() {
        List<MultiYearTemp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TNV)) {
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
     * @param temp обновленные данные
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateMultiYearTemp(MultiYearTemp temp, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_TNV)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, temp.getId());
            cStm.setDouble(3, temp.getValue());
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления " + temp.getName());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update multi year temp", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
