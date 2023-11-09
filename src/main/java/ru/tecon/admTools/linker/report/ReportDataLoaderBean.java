package ru.tecon.admTools.linker.report;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
 * 09.11.2023
 */
@Stateless
public class ReportDataLoaderBean {

    private static final String SELECT_REPORT_DATA = "select obj_name, par_name, par_memo, stat_agr_name, item_name from lnk_0001t.sel_report(?)";
    private static final String SELECT_OBJ_NAME = "select obj_name from admin.obj_object where obj_id = ?";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    public String getObjectName(int objectId) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_OBJ_NAME)) {
            stm.setInt(1, objectId);

            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getString("obj_name");
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load object name", ex);
        }
        return "";
    }

    public ReportData getReportData(int objectId) {
        return new ReportData(getObjectName(objectId), getReportRows(objectId));
    }

    public List<ReportRow> getReportRows(int objectId) {
        List<ReportRow> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_REPORT_DATA)) {
            stm.setInt(1, objectId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ReportRow(res.getString("par_name"),
                                res.getString("par_memo"),
                                res.getString("stat_agr_name"),
                                res.getString("item_name")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load report rows", ex);
        }
        return result;
    }
}
