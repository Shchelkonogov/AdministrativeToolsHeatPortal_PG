package ru.tecon.admTools.specificModel.report.ejb;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless бин реализующий интерфейс {@link ModeMapLocal},
 * для получения данных по отчет "Режимная карта"
 */
@Startup
@Stateless
@Local(ModeMapLocal.class)
public class ModeMapSB implements ModeMapLocal {

    private static final Logger LOG = Logger.getLogger(ModeMapSB.class.getName());

    private static final String SELECT_OBJECT_NAME = "select admin.get_obj_name(?)";
    private static final String SELECT_FILIAL = "select admin.get_obj_filial(?)";
    private static final String SELECT_MODE_MAP = "select * from dsp_0031t.sel_rep_karta(?)";
    private static final String SELECT_TS = "select * from dsp_0031t.sel_rep_karta_ts(?)";

    private static final String SELECT_CO = "select * from dsp_0031t.sel_rep_karta_co(?)";
    private static final String SELECT_GVS = "select * from dsp_0031t.sel_rep_karta_gvs(?)";
    private static final String SELECT_HVS = "select * from dsp_0031t.sel_rep_karta_hvs(?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @Override
    public String getName(Integer objectID) {
        try (Connection connection = ds.getConnection();
             PreparedStatement stm = connection.prepareStatement(SELECT_OBJECT_NAME)) {
            stm.setInt(1, objectID);

            ResultSet res = stm.executeQuery();
            if (res.next() && (res.getString(1) != null)) {
                String objName = res.getString(1);
                objName = objName.replaceAll("[/:*?<>\\\\|\"]", "_");
                return objName;
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load name ", e);
        }
        return null;
    }

    @Override
    public Map<String, String> loadSingleData(int objectID) {
        Map<String, String> result = new HashMap<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stmGetFilial = connect.prepareStatement(SELECT_FILIAL);
             PreparedStatement stmGetHeader = connect.prepareStatement(SELECT_MODE_MAP);
             PreparedStatement stmGetTS = connect.prepareStatement(SELECT_TS)) {
            stmGetFilial.setInt(1, objectID);
            ResultSet res = stmGetFilial.executeQuery();
            if (res.next()) {
                result.put("{filial}", res.getString(1));
            }

            stmGetHeader.setInt(1, objectID);
            res = stmGetHeader.executeQuery();
            if (res.next()) {
                result.put("{objName}", getValue(res, "obj_name"));
                result.put("{objAddress}", getValue(res, "obj_address"));
                result.put("{district}", getValue(res, "district"));
                result.put("{vvod}", getValue(res, "vvod"));
                result.put("{mks}", getValue(res, "mks"));
                result.put("{Q1}", getValue(res, "q1"));
                result.put("{Q2}", getValue(res, "q2"));
                result.put("{Q3}", getValue(res, "q3"));
                result.put("{Q4}", getValue(res, "q4"));
                result.put("{buildings}", getValue(res, "buildings"));
                result.put("{floors}", getValue(res, "floors"));
            }

            stmGetTS.setInt(1, objectID);
            res = stmGetTS.executeQuery();
            if (res.next()) {
                result.put("{T1Graph}", getValue(res, "t1_graph"));
                result.put("{T2Graph}", getValue(res, "t2_graph"));
                result.put("{P1}", getValue(res, "p1"));
                result.put("{P2}", getValue(res, "p2"));
                result.put("{P1Min}", getValue(res, "p1_min"));
                result.put("{P2Min}", getValue(res, "p2_min"));
                result.put("{P1Max}", getValue(res, "p1_max"));
                result.put("{P2Max}", getValue(res, "p2_max"));
                result.put("{Q}", getValue(res, "q"));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load single data for object: " + objectID, e);
        }
        return result;
    }

    @Override
    public Map<String, List<Map<String, String>>> loadArrayData(int objectID) {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stmGetCO = connect.prepareStatement(SELECT_CO);
             PreparedStatement stmGetGVS = connect.prepareStatement(SELECT_GVS);
             PreparedStatement stmGetHVS = connect.prepareStatement(SELECT_HVS)) {
            Map<String, String> map = new HashMap<>();
            List<Map<String, String>> list = new ArrayList<>();

            stmGetCO.setInt(1, objectID);
            ResultSet res = stmGetCO.executeQuery();
            while (res.next()) {
                map.put("{zone[]}", getValue(res, "zone"));
                map.put("{scheme[]}", getValue(res, "scheme"));
                map.put("{T3Graph[]}", getValue(res, "t3_graph"));
                map.put("{T4Graph[]}", getValue(res, "t4_graph"));
                map.put("{P3[]}", getValue(res, "p3"));
                map.put("{P4[]}", getValue(res, "p4"));
                map.put("{P3Min[]}", getValue(res, "p3_min"));
                map.put("{P4Min[]}", getValue(res, "p4_min"));
                map.put("{P3Max[]}", getValue(res, "p3_max"));
                map.put("{P4Max[]}", getValue(res, "p4_max"));
                map.put("{Q[]}", getValue(res, "q"));

                list.add(map);
            }
            result.put("CO", list);

            map = new HashMap<>();
            list = new ArrayList<>();

            stmGetGVS.setInt(1, objectID);
            res = stmGetGVS.executeQuery();
            while (res.next()) {
                map.put("{zone[]}", getValue(res, "zone"));
                map.put("{scheme[]}", getValue(res, "scheme"));
                map.put("{schemeVVP[]}", getValue(res, "scheme_vvp"));
                map.put("{P7[]}", getValue(res, "p7"));
                map.put("{P13[]}", getValue(res, "p13"));
                map.put("{T7[]}", getValue(res, "t7"));
                map.put("{T13[]}", getValue(res, "t13"));
                map.put("{P7Min[]}", getValue(res, "p7_min"));
                map.put("{P13Min[]}", getValue(res, "p13_min"));
                map.put("{P7Max[]}", getValue(res, "p7_max"));
                map.put("{P13Max[]}", getValue(res, "p13_max"));
                map.put("{Q[]}", getValue(res, "q"));

                list.add(map);
            }
            result.put("GVS", list);

            map = new HashMap<>();
            list = new ArrayList<>();

            stmGetHVS.setInt(1, objectID);
            res = stmGetHVS.executeQuery();
            while (res.next()) {
                map.put("{zone[]}", getValue(res, "zone"));
                map.put("{scheme[]}", getValue(res, "scheme"));
                map.put("{PgMin[]}", getValue(res, "pg_min"));
                map.put("{PgMax[]}", getValue(res, "pg_max"));
                map.put("{P[]}", getValue(res, "p"));
                map.put("{PMin[]}", getValue(res, "p_min"));
                map.put("{PMax[]}", getValue(res, "p_max"));
                map.put("{Q[]}", getValue(res, "q"));

                list.add(map);
            }
            result.put("HVS", list);
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load array data for object: " + objectID, e);
        }
        return result;
    }

    private String getValue(ResultSet res, String label) throws SQLException {
        return res.getString(label) == null ? "" : res.getString(label);
    }
}
