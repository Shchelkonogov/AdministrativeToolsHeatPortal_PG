package ru.tecon.admTools.systemParams.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.tecon.admTools.systemParams.model.job.JobHistory;
import ru.tecon.admTools.systemParams.model.job.Jobs;

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
 * 06.06.2025
 */
@Stateless
@LocalBean
public class JobsSB {

    @Inject
    private Logger logger;

    private static final String SELECT_JOBS = "select job, " +
            "(select comment from admin.sp_dbms_job where upper(a.what) like '%'||what||'%' limit 1) as comment, " +
            "a.what, last_date, next_date " +
            "from dbms_job.all_jobs a order by job";

    private static final String SELECT_JOBS_HISTORY = "SELECT log_date, status, actual_start_date, run_duration, additional_info " +
            "FROM dbms_job.all_scheduler_job_run_details " +
            "where job_name = ? " +
            "order by log_date desc";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка служб
     *
     * @return список служб
     */
    public List<Jobs> getJobs() {
        List<Jobs> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_JOBS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Jobs(res.getString("job"), res.getString("comment"), res.getString("what"),
                        res.getTimestamp("last_date").toLocalDateTime(), res.getTimestamp("next_date").toLocalDateTime()));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load jobs", e);
        }
        return result;
    }

    /**
     * Получение истории службы
     *
     * @return история службы
     */
    public List<JobHistory> getJobHistory(String jobName) {
        List<JobHistory> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_JOBS_HISTORY)) {
            stm.setString(1, jobName);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new JobHistory(res.getTimestamp("log_date").toLocalDateTime(), res.getString("status"),
                        res.getTimestamp("actual_start_date").toLocalDateTime(), res.getInt("run_duration"), res.getString("additional_info")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load job history", e);
        }
        return result;
    }
}
