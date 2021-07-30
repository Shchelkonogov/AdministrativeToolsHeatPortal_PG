package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.model.ParametersColor;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stateless бин для работы с формой расцветка состояния параметров
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class ParametersColorSB {

    private static final Logger LOGGER = Logger.getLogger(ParametersColorSB.class.getName());

    private static final String SELECT_PARAMETERS_COLOR = "select * from table(sys_0001t.sel_param_condition())";
    private static final String UPDATE_PARAMETER_COLOR = "{? = call sys_0001t.upd_param_cond_color(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * @return коллекция параметров с их цветами
     */
    public List<ParametersColor> getParametersColor() {
        List<ParametersColor> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAMETERS_COLOR)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                String color;
                if (res.getString(3) == null) {
                    color = "ffffff";
                } else {
                    switch (res.getString(3).toLowerCase().trim()) {
                        case "white":
                            color = "ffffff";
                            break;
                        case "black":
                            color = "000000";
                            break;
                        default:
                            color = res.getString(3);
                    }
                }
                result.add(new ParametersColor(res.getInt(1), res.getString(2), color));
            }
        } catch (SQLException e) {
            LOGGER.warning("error load parameters color data. " + e.getMessage());
        }
        return result;
    }

    /**
     * Запись нового цвета для парамтера
     * @param id id параметра
     * @param color новый цвет параметра
     * @param login имя пользователя от которого делается изменения
     * @param ip ip машины с которой делается изменение
     * @return статус выполнения 0 - успешно 1 - ошибка
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int updateParameterColor(int id, String color, String login, String ip) {
        int result = 0;
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(UPDATE_PARAMETER_COLOR)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, id);
            cStm.setString(3, color);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();
            return cStm.getInt(1);
        } catch (SQLException e) {
            LOGGER.warning("error update parameter color. " + e.getMessage());
        }
        return result;
    }
}
