package ru.tecon.admTools.systemParams.ejb;


import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.seasonChange.SeasonChangeTable;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для формы смены сезонов
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class SeasonChangeSB {
    private static final Logger LOGGER = Logger.getLogger(MainParamSB.class.getName());

    private static final String SQL_SELECT_SEASONS_CHANGE = "select * from dsp_0037t.change_season(?, ?, ?)";
    private static final String SQL_SELECT_SEASON_TABLE = "select * from dsp_0037t.sel_season_log()";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Обновление табличных значений
     * @return наименование сезона, начала и конца сезона и переключившего сезон
     */
    public List<SeasonChangeTable> getTableParams(){
        List <SeasonChangeTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_SEASON_TABLE)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new SeasonChangeTable(res.getString("season"),
                        res.getObject("start_time", LocalDateTime.class),
                        res.getObject("end_time", LocalDateTime.class),
                        res.getString("user_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Метод для переключения сезона
     */
    public void changeSeason (String seasonChangeTable, String login, String ip) throws SystemParamException{
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(SQL_SELECT_SEASONS_CHANGE)) {
            cStm.setString(1, seasonChangeTable);
            cStm.setString(2, login);
            cStm.setString(3, ip);


            LOGGER.log(Level.INFO, "Season "+seasonChangeTable );

            ResultSet resultSet = cStm.executeQuery();

            while (resultSet.next()) {
                LOGGER.log(Level.INFO,"result: " + resultSet.getString(1));
                if (resultSet.getInt(1) != 0) {
                    LOGGER.warning("season change error ");

                    throw new SystemParamException("Не удалось осуществить смену сезона");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error change season ", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
