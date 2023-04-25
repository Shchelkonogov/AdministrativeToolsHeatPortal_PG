package ru.tecon.admTools.systemParams.ejb;

import org.postgresql.util.PGobject;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.struct.StructSB;
import ru.tecon.admTools.systemParams.model.genModel.*;

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
 * Stateless bean для получения данных из база для формы обобщенная модель
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class GenModelSB {

    private static final Logger LOGGER = Logger.getLogger(StructSB.class.getName());

    private static final String SELECT_GEN_MODEL_TYPES = "select * from dsp_0029t.sel_om_tree()";
    private static final String SELECT_PARAM = "select * from dsp_0029t.sel_param(?)";
    private static final String SELECT_PARAM_PROP = "select * from dsp_0029t.sel_param_prop(?, ?)";
    private static final String SELECT_CALC_AGR_VARS = "select * from dsp_0029t.sel_calc_agr_vars(?, ?)";
    private static final String SELECT_PARAM_PROP_PER = "select * from dsp_0029t.sel_param_prop_per(?, ?)";
    private static final String SELECT_CALC_AGR_FORMULA = "select * from dsp_0029t.sel_calc_agr_formula(?, ?)";
    private static final String FUN_ADD_PARAM = "call dsp_0029t.add_param(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_ADD_CALC_AGR_FORMULA = "call dsp_0029t.add_calc_agr_formula(?, ?, ?, ?, ?, ?)";
    private static final String FUN_DEL_CALC_AGR_FORMULA = "call dsp_0029t.del_calc_agr_formula(?, ?, ?, ?)";
    private static final String FUN_DEL_PARAM = "call dsp_0029t.del_param(?, ?, ?)";
    private static final String FUN_UPD_PARAM = "call dsp_0029t.upd_param(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_PROP = "call dsp_0029t.upd_param_prop(?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_PROP_PER = "call dsp_0029t.upd_param_prop_per(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_STAT = "call dsp_0029t.upd_param_stat(?, ?, ?, ?, ?, ?)";
    private static final String SELECT_CHECK_LINKED_CALC_AGR = "select * from dsp_0029t.check_linked_calc_agr(?, ?)";
    private static final String SELECT_CHECK_NEW_FORMULA = "select * from dsp_0029t.check_new_formula(?)";
    private static final String SELECT_PARAM_LIST = "select * from dsp_0029t.sel_param_list()";
    private static final String SELECT_STAT_AGR_LIST = "select * from dsp_0029t.sel_stat_agr_list(?)";
    private static final String SELECT_OM_TREE_PARAM = "select * from dsp_0029t.sel_om_tree_param(?)";


    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * @return дерево для ОМ
     */
    public List<GMTree> getTreeParam() {
        List<GMTree> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_GEN_MODEL_TYPES)) {

            resForOmTree(result, stm);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос данных о параметре ОМ
     * @return список параметров ОМ
     */
    public List<Param> getParam(long my_id) {
        List<Param> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM)) {
            stm.setLong(1, my_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new Param(res.getLong("id"),
                        res.getLong("param_type_id"),
                        res.getString("par_code"),
                        res.getString("par_memo"),
                        res.getString("par_name"),
                        res.getLong("techproc_type_id"),
                        res.getShort("zone"),
                        res.getObject("is_graph", Long.class),
                        res.getLong("visible"),
                        res.getString("calc"),
                        res.getObject("is_decrease", Long.class),
                        res.getBoolean("edit_enable"),
                        res.getBoolean("leto_control")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос свойств аналогового стат.агр. параметра ОМ
     * @return свойства аналогового стат.агр. параметра ОМ
     */
    public List<ParamProp> getParamProp(long id, long stat_agr_id) {
        List<ParamProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_PROP)) {
            stm.setLong(1, id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new ParamProp(res.getLong("par_id"),
                        res.getLong("param_type_id"),
                        res.getLong("stat_agr_id"),
                        res.getLong("prop_id"),
                        res.getString("prop_name"),
                        res.getString("prop_val_def"),
                        res.getLong("prop_cond_great"),
                        res.getString("prop_cond_great_name"),
                        res.getLong("prop_cond_less"),
                        res.getString("prop_cond_less_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос параметров и техпроцессов для таблицы с формулой вычислимых параметров
     * @return параметры и техпроцессы для таблицы с формулой вычислимых параметров
     */
    public List<CalcAgrVars> getCalcAgrVars(long par_id, long stat_agr_id) {
        List<CalcAgrVars> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CALC_AGR_VARS)) {
            stm.setLong(1, par_id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                ParamList paramList = new ParamList(res.getLong("par_id"),
                        res.getString("par_memo"), res.getString("par_name"));
                StatAgrList statAgrList = new StatAgrList(res.getLong("stat_agr_id"), res.getString("stat_agr_code"));
                result.add(new CalcAgrVars(res.getLong("calc_par_id"),
                        res.getLong("calc_stat_agr_id"),
                        res.getString("variable"),
                        paramList, statAgrList));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос перечисленй перечислимого стат.агр. параметра ОМ
     * @return перечисления перечислимого стат.агр. параметра ОМ
     */
    public List<ParamPropPer> getParamPropPer(long id, long stat_agr_id) {
        List<ParamPropPer> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_PROP_PER)) {
            stm.setLong(1, id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new ParamPropPer(res.getLong("par_id"),
                        res.getLong("param_type_id"),
                        res.getLong("stat_agr_id"),
                        res.getLong("enum_code"),
                        res.getString("prop_val"),
                        res.getLong("prop_cond")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос формулы вычисления вычислимого стат.агрегата параметра
     * @return формулу вычисления вычислимого стат.агрегата параметра
     */
    public String getCalcAgrFormula(long par_id, long stat_agr_id) throws SystemParamException {
        String result;
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CALC_AGR_FORMULA)) {
            stm.setLong(1, par_id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();
            res.next() ;
            result=res.getString(1);
            LOGGER.log(Level.INFO, "result: " + res.getString(1));

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "getting Calc Agr formula error", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
        return result;
    }

    /**
     * Создание нового параметра
     *
     * @param addParam параметр
     * @param login    имя пользователя
     * @param ip       адрес пользователя
     * @throws SystemParamException в случае ошибки создания новой единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long addParam(Long grandparentId, Long parentId,Param addParam, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_PARAM)) {
            cStm.setLong(1, grandparentId);
            cStm.setLong(2, parentId);
            cStm.setString(3, addParam.getPar_code());
            cStm.setString(4, addParam.getPar_memo());
            cStm.setString(5, addParam.getPar_name());
            cStm.setShort(6, addParam.getZone());
            cStm.setObject(7, addParam.getIs_graph());
            cStm.setLong(8, addParam.getVisible());
            cStm.setObject(9, addParam.getIs_decrease());
            cStm.setShort(10, addParam.getEdit_enableShort());
            cStm.setShort(11, addParam.getLeto_controlShort());
            cStm.registerOutParameter(12, Types.BIGINT);
            cStm.setString(13, login);
            cStm.setString(14, ip);

            cStm.executeUpdate();

            LOGGER.info("add param " + addParam.getPar_name());
            return cStm.getLong(12);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add param", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Добавление новой формулы
     *
     * @param calcAgrFormula формула
     * @param login           имя пользователя
     * @param ip              адрес пользователя
     * @throws SystemParamException в случае ошибки создания новой единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addCalcAgrFormula(long par_id, long stat_agr_id, String formula, List<CalcAgrFormula> calcAgrFormula,
                                  String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
            CallableStatement cStm = connect.prepareCall(FUN_ADD_CALC_AGR_FORMULA)) {
            PGobject [] object = new PGobject[calcAgrFormula.size()];
            for(int i=0; i<object.length; i++){
                PGobject pGobject = new PGobject();
                pGobject.setType("dsp_0029t.vars_rec");
                pGobject.setValue(String.valueOf(calcAgrFormula.get(i)));
                object[i]=pGobject;
            }
            Array array = connect.createArrayOf("dsp_0029t.vars_rec", object);
            cStm.setLong(1, par_id);
            cStm.setLong(2, stat_agr_id);
            cStm.setString(3, formula);
            cStm.setArray(4, array);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("add CalcAgrFormula" + calcAgrFormula);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add CalcAgrFormula", e);
            throw new SystemParamException("Проверьте корректность заполнения");
        }
    }

    /**
     * Удаление формулы и расшифровки
     *
     * @param par_id         id параметра
     * @param stat_agr_id    id свойства параметра
     * @param login          имя пользователя
     * @param ip             адрес пользователя
     * @throws SystemParamException в случае ошибки удаления единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeCalcAgrFormula(long par_id, long stat_agr_id, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_CALC_AGR_FORMULA)) {
            cStm.setLong(1, par_id);
            cStm.setLong(2, stat_agr_id);
            cStm.setString(3, login);
            cStm.setString(4, ip);

            cStm.executeUpdate();

            LOGGER.info("remove calcAgrFormula Par_id " + par_id +
                    " Stat_agr_id " + stat_agr_id);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error remove calcAgrFormula", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление параметра
     * @param selectedRowID id параметра
     * @param login имя пользователя
     * @param ip    адрес пользователя
     * @throws SystemParamException в случае ошибки удаления единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeParam(Long selectedRowID, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_PARAM)) {
            cStm.setLong(1, selectedRowID);
            cStm.setString(2, login);
            cStm.setString(3, ip);

            cStm.executeUpdate();

            LOGGER.info("remove param " + selectedRowID);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error remove Param", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Обновление параметра
     *
     * @param param параметр
     * @param login имя пользователя
     * @param ip    адрес пользователя
     * @throws SystemParamException в случае ошибки обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updParam(Param param, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_PARAM)) {
            cStm.setLong(1, param.getId());
            cStm.setString(2, param.getPar_code());
            cStm.setString(3, param.getPar_memo());
            cStm.setString(4, param.getPar_name());
            cStm.setShort(5, param.getZone());
            cStm.setObject(6, param.getIs_graph());
            cStm.setLong(7, param.getVisible());
            cStm.setObject(8, param.getIs_decrease());
            cStm.setShort(9, param.getEdit_enableShort());
            cStm.setShort(10, param.getLeto_controlShort());
            cStm.setString(11, login);
            cStm.setString(12, ip);

            cStm.executeUpdate();

            LOGGER.info("update param " + param.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update Param", e);
            throw new SystemParamException("Параметр не был обновлен");
        }
    }

    /**
     * Обновление аналогового
     *
     * @param paramProp параметр
     * @param login     имя пользователя
     * @param ip        адрес пользователя
     * @throws SystemParamException в случае ошибки обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updParamProp(ParamProp paramProp, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_PARAM_PROP)) {
            cStm.setLong(1, paramProp.getPar_id());
            cStm.setLong(2, paramProp.getStat_agr_id());
            cStm.setLong(3, paramProp.getProp_id());
            cStm.setString(4, paramProp.getProp_val_def());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("update param prop " + paramProp.getPar_id());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update Param prop ", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Обновление перечислимого
     *
     * @param paramPropPer параметр
     * @param login        имя пользователя
     * @param ip           адрес пользователя
     * @throws SystemParamException в случае ошибки обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updParamPropPer(ParamPropPer paramPropPer, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_PARAM_PROP_PER)) {
            cStm.setLong(1, paramPropPer.getPar_id());
            cStm.setLong(2, paramPropPer.getStat_agr_id());
            cStm.setLong(3, paramPropPer.getEnum_code());
            cStm.setString(4, paramPropPer.getProp_val());
            cStm.setLong(5, paramPropPer.getProp_cond());
            cStm.setString(6, login);
            cStm.setString(7, ip);

            cStm.executeUpdate();

            LOGGER.info("update param prop per" + paramPropPer.getPar_id());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update Param prop per", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }


    /**
     * Обновление галки визуализации
     *
     * @param selectedRow параметр
     * @param login     имя пользователя
     * @param ip        адрес пользователя
     * @throws SystemParamException в случае ошибки обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updParamStat(Long parentId, Long selectedRowID, GMTree selectedRow, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_PARAM_STAT)) {
            cStm.setLong(1, parentId);
            cStm.setLong(2, selectedRowID);
            cStm.setShort(3, selectedRow.getMeasure_id());
            cStm.setShort(4, selectedRow.getVis_statShort());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("update param stat " + selectedRow.getMeasure_id());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update Param stat ", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Функция проверяет слинкован ли вычислимый стат.агр. параметра (проверка перед удалением)
     */
    public Short checkLinkedCalcAgr(Long parentId, Long selectedRowID) throws SystemParamException{
        short result=2;
        try (Connection connect = ds.getConnection();
             PreparedStatement cStm = connect.prepareStatement(SELECT_CHECK_LINKED_CALC_AGR)) {
            cStm.setLong(1, parentId);
            cStm.setLong(2,selectedRowID);

            ResultSet resultSet = cStm.executeQuery();

            while (resultSet.next()) {
                result = resultSet.getShort(1);
                LOGGER.log(Level.INFO, "result: " + resultSet.getInt(1));
                if (resultSet.getInt(1) != 0) {
                    LOGGER.warning("linked ");

                    throw new SystemParamException("Вычислимый стат.агр. параметра слинкован");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "check instr error", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
        return result;
    }

    /**
     * Проверка правильности ввода формулы
     */
    public List<String> checkNewFormula(String formula) throws SystemParamException{
        List<String> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CHECK_NEW_FORMULA)) {
            stm.setString(1, formula);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add((res.getString("variable")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Проверьте правильность ввода!");
        }
        return result;
    }

    /**
     * Запрос списка для выбора параметра для формулы вычисления
     * @return Список для выбора параметра для формулы вычисления
     */
    public List<ParamList> getParamList() {
        List<ParamList> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_LIST)) {

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new ParamList(res.getLong("id"),
                        res.getString("par_memo"),
                        res.getString("par_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос списка для выбора стат.агрегата для параметра из формулы вычисления
     * @return Список выбора стат.агрегата для параметра из формулы вычисления
     */
    public List<StatAgrList> getStatAgrList(Long par_id) {
        List<StatAgrList> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STAT_AGR_LIST)) {
            stm.setLong(1, par_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new StatAgrList(res.getLong("stat_agr_id"),
                        res.getString("stat_agr_code")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * @return дерево для ОМ
     */
    public List<GMTree> getTreeParamPP(String id) {
        List<GMTree> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_OM_TREE_PARAM)) {
            stm.setString(1, id);

            resForOmTree(result, stm);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    private void resForOmTree(List<GMTree> result, PreparedStatement stm) throws SQLException {
        ResultSet res = stm.executeQuery();

        while (res.next()) {
            result.add(new GMTree(res.getString("id"),
                    res.getString("name"),
                    res.getString("parent"),
                    res.getLong("my_id"),
                    res.getString("my_type"),
                    res.getString("categ"),
                    res.getShort("measure_id"),
                    res.getString("measure_name"),
                    res.getBoolean("vis_stat")));
        }
    }
}