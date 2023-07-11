package ru.tecon.admTools.specificModel.report.ejb;

import ru.tecon.admTools.specificModel.report.model.ModeControlModel;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Startup;
import javax.ejb.Stateless;
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
 * Stateless бин реализующий интерфейс {@link ModeControlLocal},
 * для получения данных по отчет "Контроль режима"
 */
@Startup
@Stateless
@Local(ModeControlLocal.class)
public class ModeControlSB implements ModeControlLocal {

    private static final Logger LOG = Logger.getLogger(ModeControlSB.class.getName());

    private static final String SELECT_PARAM_NAME = "select par_name from admin.dz_param where id = ?";
    private static final String SELECT_STRUCT_PATH = "select admin.get_struct_path_all(?)";
    private static final String SELECT_REPORT_DATA = "select * from dsp_0031t.sel_rep_control(?, ?, ?, ?, ?, ?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @Override
    public String getParamName(int paramID) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_NAME)) {
            stm.setInt(1, paramID);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getString(1);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load param name", e);
        }
        return "";
    }

    @Override
    public String getStructPath(int structID) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STRUCT_PATH)) {
            stm.setInt(1, structID);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getString(1);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load struct path", e);
        }
        return "";
    }

    @Override
    public List<ModeControlModel> getData(int objType, int structID, int filterType, String filter, int paramID, String user) {
        List<ModeControlModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_REPORT_DATA)) {
            stm.setInt(1, objType);
            stm.setInt(2, structID);
            stm.setInt(3, filterType);
            stm.setString(4, filter);
            stm.setInt(5, paramID);
            stm.setString(6, user);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ModeControlModel(res.getString("obj_name"),
                        res.getString("obj_address"),
                        res.getString("graph_name"),
                        res.getString("decrease"),
                        res.getString("t_min"),
                        res.getString("t_max"),
                        res.getString("a_min"),
                        res.getString("a_max"),
                        res.getString("stat_agr_name")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load data", e);
        }
        return result;
    }
}
