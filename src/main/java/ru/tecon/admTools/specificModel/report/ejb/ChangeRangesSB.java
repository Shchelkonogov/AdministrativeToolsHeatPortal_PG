package ru.tecon.admTools.specificModel.report.ejb;

import ru.tecon.admTools.specificModel.report.model.ChangeRangesModel;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless бин, который реализует local интерфейс {@link ChangeRangesLocal}
 */
@Startup
@Stateless
@Local(ChangeRangesLocal.class)
public class ChangeRangesSB implements ChangeRangesLocal {

    private static final Logger LOG = Logger.getLogger(ChangeRangesSB.class.getName());

    private static final String SELECT_OBJECT_PATH = "select get_obj_path_all(?) from dual";
    private static final String SELECT_STRUCT_PATH = "select get_struct_path_all(?) from dual";

    private static final String SELECT_LOAD_DATA = "select to_char(change_date, 'dd.mm.yyyy HH24:mi:ss') as change_date, " +
            "obj_name, par_memo, stat_agr_name, range_name, old_val, new_val, user_name " +
            "from table(dsp_0031t.sel_rep_change_ranges(?, ?, ?, ?, ?, to_date(?, 'dd.mm.yyyy'), ?))";
    private static final String SELECT_LOAD_DATA_ECO = "select to_char(change_date, 'dd.mm.yyyy HH24:mi:ss') as change_date, " +
            "obj_name, par_memo, stat_agr_name, range_name, old_val, new_val, user_name " +
            "from table(dsp_0050t.sel_rep_change_ranges(?, ?, ?, ?, ?, to_date(?, 'dd.mm.yyyy'), ?))";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @Override
    public String getPath(Integer structID, Integer objectID) {
        try (Connection connect = ds.getConnection();
             PreparedStatement getObjectPath = connect.prepareStatement(SELECT_OBJECT_PATH);
             PreparedStatement getStructPath = connect.prepareStatement(SELECT_STRUCT_PATH)) {
            if (structID == null) {
                getObjectPath.setInt(1, objectID);
                ResultSet res = getObjectPath.executeQuery();
                if (res.next()) {
                    return res.getString(1);
                }
            }
            if (objectID == null) {
                getStructPath.setInt(1, structID);
                ResultSet res = getStructPath.executeQuery();
                if (res.next()) {
                    return res.getString(1);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load path", e);
        }
        return "";
    }

    @Override
    public List<ChangeRangesModel> loadReportData(int objType, Integer structID, Integer objID, int filterType,
                                                  String filter, String date, String user) {
        return loadReportData(objType, structID, objID, filterType, filter, date, user, false);
    }

    @Override
    public List<ChangeRangesModel> loadReportData(int objType, Integer structID, Integer objID, int filterType,
                                                  String filter, String date, String user, boolean eco) {
        List<ChangeRangesModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(eco ? SELECT_LOAD_DATA_ECO : SELECT_LOAD_DATA)) {
            stm.setInt(1, objType);
            if (((structID == null) && (objID == null)) || ((structID != null) && (objID != null))) {
                return result;
            }
            if (structID == null) {
                stm.setNull(2, Types.INTEGER);
            } else {
                stm.setInt(2, structID);
            }
            if (objID == null) {
                stm.setNull(3, Types.INTEGER);
            } else {
                stm.setInt(3, objID);
            }
            stm.setInt(4, filterType);
            stm.setString(5, filter);
            stm.setString(6, date);
            stm.setString(7, user);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ChangeRangesModel(res.getString("obj_name"),
                        res.getString("change_date"),
                        res.getString("par_memo"),
                        res.getString("stat_agr_name"),
                        res.getString("range_name"),
                        res.getString("old_val"),
                        res.getString("new_val"),
                        res.getString("user_name")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load report data", e);
        }
        return result;
    }
}
