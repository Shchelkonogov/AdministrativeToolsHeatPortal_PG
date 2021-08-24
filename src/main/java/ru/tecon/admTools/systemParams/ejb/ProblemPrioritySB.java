package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.ParametersColor;
import ru.tecon.admTools.systemParams.model.ProblemPriority;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless бин для работы с формой приоритет проблем
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class ProblemPrioritySB {

    private static final Logger LOGGER = Logger.getLogger(ProblemPrioritySB.class.getName());

    private static final String SELECT_PROBLEM_PRIORITY = "select * from table(dsp_0090t.sel_problem_cat_list())";
    private static final String UPDATE_PROBLEM_PRIORITY = "{? = call dsp_0090t.upd_problem_cat(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * @return коллекция проблем с приоритетом
     */
    public List<ProblemPriority> getProblemPriority() {
        List<ProblemPriority> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PROBLEM_PRIORITY)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ProblemPriority(res.getInt(1), res.getString(2), res.getInt(3)));
            }
        } catch (SQLException e) {
            LOGGER.warning("error load problem priority data. " + e.getMessage());
        }
        return result;
    }

    /**
     * Запись нового приоритета для проблемы
     * @param id id проблемы
     * @param priority новый приритет проблемы
     * @param login имя пользователя от которого делается изменения
     * @param ip ip машины с которой делается изменение
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateProblemPriority(int id, int priority, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(UPDATE_PROBLEM_PRIORITY)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, id);
            cStm.setInt(3, priority);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();
            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка записи проблемы " + id + " приоритет " + priority);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
