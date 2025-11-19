package ru.tecon.admTools.systemParams.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.tecon.admTools.systemParams.model.integration.Asot;
import ru.tecon.admTools.systemParams.model.integration.Assd;
import ru.tecon.admTools.systemParams.model.integration.Eod;
import ru.tecon.admTools.systemParams.model.integration.Esm;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 18.11.2025
 */
@Stateless
@LocalBean
public class IntegrationSB {

    private static final String SELECT_ASOT = "select * from asot.get_asot_log()";
    private static final String SELECT_ESM = "select * from diu_0004t.esm_history(50)";
    private static final String SELECT_EOD = "select * from mmc.view_mmc_log()";
    private static final String SELECT_ASSD = "select * from admin.get_assd_log()";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение данных по АСОТ
     *
     * @return данные АСОТ
     */
    public List<Asot> getAsotData() {
        List<Asot> result = new ArrayList<>();

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_ASOT)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Asot(
                        res.getString("table_name"),
                        res.getString("muid"),
                        res.getTimestamp("create_date").toLocalDateTime(),
                        res.getString("operation"),
                        res.getString("status_message")
                        )
                );
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load asot data", e);
        }
        return result;
    }

    /**
     * Получение данных по ЕСМ
     *
     * @return данные ЕСМ
     */
    public List<Esm> getEsmData() {
        List<Esm> result = new ArrayList<>();

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_ESM)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Esm(
                        res.getString("login"),
                        res.getString("ip"),
                        res.getTimestamp("date_").toLocalDateTime(),
                        res.getString("action_"),
                        res.getString("obj_name"),
                        res.getString("obj_prop_name"),
                        res.getString("file_name"),
                        res.getString("esm_guid")
                        )
                );
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load esm data", e);
        }
        return result;
    }

    /**
     * Получение данных по ЕОД
     *
     * @return данные ЕОД
     */
    public List<Eod> getEodData() {
        List<Eod> result = new ArrayList<>();

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_EOD)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Eod(
                                res.getTimestamp("sel_date").toLocalDateTime(),
                                res.getString("user_name"),
                                res.getString("view_name")
                        )
                );
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load eod data", e);
        }
        return result;
    }

    /**
     * Получение данных по АССД
     *
     * @return данные АССД
     */
    public List<Assd> getAssdData() {
        List<Assd> result = new ArrayList<>();

        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_ASSD)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Assd(
                                res.getInt("obj_id"),
                                res.getInt("par_id"),
                                res.getString("par_value"),
                                res.getTimestamp("time_stamp").toLocalDateTime(),
                                res.getTimestamp("updated_when").toLocalDateTime()
                        )
                );
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load assd data", e);
        }
        return result;
    }
}
