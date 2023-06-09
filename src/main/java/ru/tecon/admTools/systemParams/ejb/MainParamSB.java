package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.mainParam.MPTable;
import ru.tecon.admTools.systemParams.model.mainParam.TechProc;
import ru.tecon.admTools.systemParams.model.mainParam.TechProcParam;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для получения данных из база для формы основные параметры
 *
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class MainParamSB {

    private static final Logger LOGGER = Logger.getLogger(MainParamSB.class.getName());

    private static final String SQL_SELECT_PARAMS_BY_ID = "select * from sys_0001t.sel_obj_type_techproc(?)";
    private static final String SQL_SELECT_SELECTED_PARAMS = "select * from dsp_0036t.list_basic_param(?, ?)";
    private static final String FUN_DEL_PARAMS = "select * from dsp_0036t.del_basic_param(?, ?, ?, ?, ?, ?)";
    private static final String FUN_ADD_PARAMS = "select * from dsp_0036t.add_basic_param(?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BASIC_PARAMS_BY_ID = "select * from dsp_0036t.list_add_basic_param(?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Обновление типа техпроцесса
     *
     * @return список техпроцессов
     */
    public LinkedList<TechProc> getRightPartSelectOneMenuParam(int objId) {
        LinkedList<TechProc> result = new LinkedList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_PARAMS_BY_ID)) {
            stm.setInt(1, objId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new TechProc(res.getInt("obj_type_id"),
                        res.getInt("techproc_type_id"),
                        res.getString("techproc_type_name"),
                        res.getString("techproc_type_char")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }

        return result;
    }

    /**
     * Обновление табличных значений
     *
     * @return наименование параметра техпроцесса и обозначение
     */
    public List<MPTable> getTableParam(int objId, int techprId) {
        List<MPTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_SELECTED_PARAMS)) {
            stm.setInt(1, objId);
            stm.setInt(2, techprId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new MPTable(res.getInt("obj_type_id"),
                        res.getInt("techproc_type_id"),
                        res.getInt("param_type_id"),
                        res.getInt("par_id"),
                        res.getString("par_name"),
                        res.getString("par_memo")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Обновление параметров техпроцесса
     *
     * @return список параметров техпроцессов
     */
    public List<TechProcParam> getParamsOfTechProc(int techprId) {
        List<TechProcParam> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_BASIC_PARAMS_BY_ID)) {
            stm.setInt(1, techprId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new TechProcParam(res.getInt("id"),
                        res.getInt("param_type_id"),
                        res.getInt("techproc_type_id"),
                        res.getString("par_memo"),
                        res.getString("par_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }

        return result;
    }

    /**
     * Удаление параметров техпроцесса из таблицы
     */
    public void removeParamFromTable(int objId, int techprId, int parTypeId, int id, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(FUN_DEL_PARAMS)) {
            cStm.setLong(1, objId);
            cStm.setLong(2, techprId);
            cStm.setLong(3, parTypeId);
            cStm.setLong(4, id);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            LOGGER.log(Level.INFO, objId + " " + techprId + " " + parTypeId + " " + id);

            ResultSet resultSet = cStm.executeQuery();
            while (resultSet.next()) {
                LOGGER.log(Level.INFO, "result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) != 0) {
                    LOGGER.warning("deleting params at table error ");

                    throw new SystemParamException("Не удалось осуществить удаление объекта");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error del tableParam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }

    }

    /**
     * Добавление параметров техпроцесса в таблицу
     */
    public void addParamAtTable(int objId, int techprId, int parTypeId, int id, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(FUN_ADD_PARAMS)) {
            cStm.setLong(1, objId);
            cStm.setLong(2, techprId);
            cStm.setLong(3, parTypeId);
            cStm.setLong(4, id);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            ResultSet resultSet = cStm.executeQuery();
            while (resultSet.next()) {
                LOGGER.log(Level.INFO, "result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) != 0) {
                    LOGGER.warning("adding params at table error ");

                    throw new SystemParamException("Не удалось осуществить добавление параметра");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add tableParam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
