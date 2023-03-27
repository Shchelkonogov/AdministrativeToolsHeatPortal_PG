package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.ObjectType;
import ru.tecon.admTools.systemParams.model.mainParam.*;

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
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class MainParamSB {
    private static final Logger LOGGER = Logger.getLogger(MainParamSB.class.getName());

    private static final String SQL_SELECT_PARAMS_BY_ID = "select * from sys_0001t.sel_obj_type_techproc(?)";
    private static final String SQL_SELECT_SELECTED_PARAMS = "select * from dsp_0036t.list_basic_param(?, ?)";
    private static final String FUN_DEL_PARAMS = "select * from dsp_0036t.del_basic_param(?, ?, ?, ?, ?, ?)";
    private static final String FUN_ADD_PARAMS= "select * from dsp_0036t.add_basic_param(?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BPARAMS_BY_ID = "select * from dsp_0036t.list_add_basic_param(?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Обновление типа техпроцесса
     * @return список техпроцессов
     */
    public LinkedList<TechProc> getRightpartSelectOneMenuParam(int objid) {
        LinkedList<TechProc> result = new LinkedList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_PARAMS_BY_ID)) {
            stm.setInt(1, objid);
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
     * @return наименование параметра техпроцесса и обозначение
     */
    public List<MPTable> getTabeParam(int objid, int techprid) {
        List<MPTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_SELECTED_PARAMS)) {
            stm.setInt(1, objid);
            stm.setInt(2, techprid);
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
     * @return список параметров техпроцессов
     */
    public List<TechProcParam> getParametrsOfTechProc(int techprid) {
        List<TechProcParam> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SQL_SELECT_BPARAMS_BY_ID)) {
            stm.setInt(1, techprid);
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
    public void removeParamFromTable(int objid, int techprid, int partypeid, int id, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(FUN_DEL_PARAMS)) {
            cStm.setLong(1, objid);
            cStm.setLong(2, techprid);
            cStm.setLong(3, partypeid);
            cStm.setLong(4, id);
            cStm.setString(5, login);
            cStm.setString(6, ip);


            LOGGER.log(Level.INFO,objid + " " + techprid + " " + partypeid + " " + id);

            ResultSet resultSet = cStm.executeQuery();
            while (resultSet.next()) {
                LOGGER.log(Level.INFO,"result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) !=0) {
                    LOGGER.warning("deleting params at table error ");

                    throw new SystemParamException("Не удалось осуществить удаление объекта");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error del tableparam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }

    }

    /**
     * Добавление параметров техпроцесса в таблицу
     */
    public void addParamAtTable(int objid, int techprid, int partypeid, int id, String login, String ip) throws SystemParamException{
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(FUN_ADD_PARAMS)) {
            cStm.setLong(1, objid);
            cStm.setLong(2, techprid);
            cStm.setLong(3, partypeid);
            cStm.setLong(4, id);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            ResultSet resultSet = cStm.executeQuery();
            while (resultSet.next()) {
                LOGGER.log(Level.INFO,"result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) !=0) {
                    LOGGER.warning("adding params at table error ");

                    throw new SystemParamException("Не удалось осуществить добавление параметра");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add tableparam", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
