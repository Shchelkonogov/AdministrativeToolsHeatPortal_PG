package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.plannedOutages.ShutdownRange;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для формы плановые отключения
 *
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class PlannedOutagesSB {

    private static final String SQL_GET_BEG_SHUTDOWN_RANGE = "select * from dsp_0037t.get_beg_shutdown_range()";
    private static final String SQL_SET_BEG_SHUTDOWN_RANGE = "select * from dsp_0037t.set_beg_shutdown_range(?)";
    private static final String SQL_GET_END_SHUTDOWN_RANGE = "select * from dsp_0037t.get_end_shutdown_range()";
    private static final String SQL_SET_END_SHUTDOWN_RANGE = "select * from dsp_0037t.set_end_shutdown_range(?)";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение значения границы возможного ввода начала планового отключения в днях от текущей даты
     *
     * @return количество дней
     */
    public ShutdownRange getBegShutdownRange() {
        ShutdownRange result = new ShutdownRange();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_GET_BEG_SHUTDOWN_RANGE)) {
            ResultSet res = stm.executeQuery();

            if (res.next()) {
                result = new ShutdownRange(res.getInt("get_beg_shutdown_range"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Получение значения границы возможного ввода конца планового отключения в днях от даты начала планового отключения
     *
     * @return количество дней
     */
    public ShutdownRange getEndShutdownRange() {
        ShutdownRange result = new ShutdownRange();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_GET_END_SHUTDOWN_RANGE)) {
            ResultSet res = stm.executeQuery();

            if (res.next()) {
                result = new ShutdownRange(res.getInt("get_end_shutdown_range"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Изменение значения границы возможного ввода начала планового отключения в днях от текущей даты
     */
    public void setBegShutdownRange(int p_val) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(SQL_SET_BEG_SHUTDOWN_RANGE)) {
            cStm.setInt(1, p_val);

            logger.log(Level.INFO, "Beg Shutdown Range changed to " + p_val);

            ResultSet resultSet = cStm.executeQuery();

            if (resultSet.next()) {
                logger.log(Level.INFO, "result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) != 0) {
                    logger.warning("changing Beg Shutdown Range changed error");

                    throw new SystemParamException("Не удалось изменить значение границы возможного ввода начала планового отключения");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error del tableParam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Изменение значения границы возможного ввода конца планового отключения в днях от даты начала планового отключения
     */
    public void setEndShutdownRange(int p_val) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(SQL_SET_END_SHUTDOWN_RANGE)) {
            cStm.setInt(1, p_val);

            logger.log(Level.INFO, "End Shutdown Range changed to " + p_val);

            ResultSet resultSet = cStm.executeQuery();

            if (resultSet.next()) {
                logger.log(Level.INFO, "result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) != 0) {
                    logger.warning("changing End Shutdown Range changed error");

                    throw new SystemParamException("Не удалось изменить значение границы возможного ввода конца планового отключения");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error del tableParam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
