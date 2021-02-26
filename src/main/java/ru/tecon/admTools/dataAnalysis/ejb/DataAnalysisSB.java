package ru.tecon.admTools.dataAnalysis.ejb;

import ru.tecon.admTools.dataAnalysis.model.Criterion;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
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
 * Stateless bean для получения данных для формы Анализ достоверности
 */
@Stateless
@LocalBean
public class DataAnalysisSB {

    private static final Logger LOGGER = Logger.getLogger(DataAnalysisSB.class.getName());

    private static final String SELECT_DAY_CRITERION = "select crit_id, alt_crit_name from dz_quality_criteria where odpu is null";
    private static final String SELECT_MONTH_CRITERION = "select crit_id, alt_crit_name from dz_quality_criteria where odpu = 1";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка суточных критериев
     * @return список критериев
     */
    public List<Criterion> getDayCriterion() {
        return getCriterion(SELECT_DAY_CRITERION);
    }

    /**
     * Получение списка месячных критериев
     * @return список критериев
     */
    public List<Criterion> getMonthCriterion() {
        return getCriterion(SELECT_MONTH_CRITERION);
    }

    private List<Criterion> getCriterion(String select) {
        List<Criterion> criterion = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            stm.executeQuery();
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                criterion.add(new Criterion(res.getInt("crit_id"), res.getString("alt_crit_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error load criterion for select " + select, e);
        }
        return criterion;
    }
}
