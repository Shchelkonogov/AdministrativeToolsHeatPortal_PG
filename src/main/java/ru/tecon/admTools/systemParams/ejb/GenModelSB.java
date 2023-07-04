package ru.tecon.admTools.systemParams.ejb;

import org.postgresql.util.PGobject;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.ejb.temperature.TemperatureLocal;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.genModel.*;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureStatus;
import ru.tecon.admTools.utils.AdmTools;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для получения данных из база для формы обобщенная модель
 *
 * @author Aleksey Sergeev
 */
@Stateless
@LocalBean
public class GenModelSB {

    private static final String SELECT_GEN_MODEL_TYPES = "select * from dsp_0029t.sel_om_tree()";
    private static final String SELECT_PARAM = "select * from dsp_0029t.sel_param(?)";
    private static final String SELECT_PARAM_PROP = "select * from dsp_0029t.sel_param_prop(?, ?)";
    private static final String SELECT_CALC_AGR_VARS = "select * from dsp_0029t.sel_calc_agr_vars(?, ?) order by variable";
    private static final String SELECT_PARAM_PROP_PER = "select * from dsp_0029t.sel_param_prop_per(?, ?)";
    private static final String SELECT_CALC_AGR_FORMULA = "select * from dsp_0029t.sel_calc_agr_formula(?, ?)";
    private static final String FUN_ADD_PARAM = "call dsp_0029t.add_param(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_ADD_CALC_AGR_FORMULA = "call dsp_0029t.add_calc_agr_formula(?, ?, ?, ?, ?, ?)";
    private static final String FUN_DEL_CALC_AGR_FORMULA = "call dsp_0029t.del_calc_agr_formula(?, ?, ?, ?)";
    private static final String FUN_DEL_PARAM = "call dsp_0029t.del_param(?, ?, ?)";
    private static final String FUN_UPD_PARAM = "call dsp_0029t.upd_param(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_PROP = "call dsp_0029t.upd_param_prop(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_PROP_PER = "call dsp_0029t.upd_param_prop_per(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_PARAM_STAT = "call dsp_0029t.upd_param_stat(?, ?, ?, ?, ?, ?)";
    private static final String SELECT_CHECK_LINKED_CALC_AGR = "select * from dsp_0029t.check_linked_calc_agr(?, ?)";
    private static final String SELECT_CHECK_NEW_FORMULA = "select * from dsp_0029t.check_new_formula(?) order by variable";
    private static final String SELECT_PARAM_LIST = "select * from dsp_0029t.sel_param_list()";
    private static final String SELECT_STAT_AGR_LIST = "select * from dsp_0029t.sel_stat_agr_list(?)";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @EJB(beanName = "tempGraphSB")
    private TemperatureLocal tempGraphBean;

    @EJB(beanName = "dailyReductionSB")
    private TemperatureLocal dailyReductionBean;

    /**
     * @return дерево для ОМ
     */
    public List<GMTree> getTreeParam() {
        List<GMTree> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_GEN_MODEL_TYPES)) {
            ResultSet res = stm.executeQuery();

            while (res.next()) {
                GMTree gmTree = new GMTree(res.getString("id"),
                        res.getString("name"),
                        res.getString("parent"),
                        res.getLong("my_id"),
                        res.getString("my_type"),
                        res.getString("categ"),
                        res.getBoolean("vis_stat"));

                if (res.getObject("measure_id") != null) {
                    Measure measure = new Measure();
                    measure.setId(res.getInt("measure_id"));
                    measure.setShortName(res.getString("measure_name"));
                    gmTree.setMeasure(measure);
                }

                result.add(gmTree);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос данных о параметре ОМ
     *
     * @return список параметров ОМ
     */
    public Param getParam(long my_id) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM)) {
            stm.setLong(1, my_id);

            ResultSet res = stm.executeQuery();

            if (res.next()) {
                Param result = new Param(res.getLong("id"),
                        res.getLong("param_type_id"),
                        res.getString("par_code"),
                        res.getString("par_memo"),
                        res.getString("par_name"),
                        res.getLong("techproc_type_id"),
                        res.getShort("zone"),
                        res.getLong("visible"),
                        res.getString("calc"),
                        res.getBoolean("edit_enable"),
                        res.getBoolean("leto_control"));

                if (res.getObject("is_graph") != null) {
                    result.setGraph(tempGraphBean.findById(res.getInt("is_graph")));
                }

                if (res.getObject("is_decrease") != null) {
                    result.setDecrease(dailyReductionBean.findById(res.getInt("is_decrease")));
                }

                if (res.getObject("is_pressure") != null) {
                    result.setOptValue(res.getBigDecimal("is_pressure"));
                }

                return result;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return null;
    }

    /**
     * Запрос свойств аналогового статистического агрегата параметра ОМ
     *
     * @return Свойства аналогового статистического агрегата параметра ОМ
     */
    public List<ParamProp> getParamProp(long id, long stat_agr_id) {
        List<ParamProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_PROP)) {
            stm.setLong(1, id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                Condition greatCond = new Condition(res.getInt("prop_cond_great"), res.getString("prop_cond_great_name"));
                Condition lessCond = new Condition(res.getInt("prop_cond_less"), res.getString("prop_cond_less_name"));
                result.add(new ParamProp(res.getLong("par_id"),
                        res.getLong("param_type_id"),
                        res.getLong("stat_agr_id"),
                        res.getLong("prop_id"),
                        res.getString("prop_name"),
                        res.getString("prop_val_def"), greatCond, lessCond));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос перечислений перечислимого стат.агр. параметра ОМ
     *
     * @return Перечисления перечислимого стат.агр. параметра ОМ
     */
    public List<ParamPropPer> getParamPropPer(long id, long stat_agr_id) {
        List<ParamPropPer> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_PROP_PER)) {
            stm.setLong(1, id);
            stm.setLong(2, stat_agr_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                Condition propCond = new Condition(res.getInt("prop_cond"), res.getString("prop_val"));
                result.add(new ParamPropPer(res.getLong("par_id"),
                        res.getLong("param_type_id"),
                        res.getLong("stat_agr_id"),
                        res.getLong("enum_code"),
                        propCond));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Получения данных о формуле для вычислимых параметров
     *
     * @param parId id параметра
     * @param statAgrId id статистического агрегата
     * @return список данных по формуле
     */
    public ParamPropCalc getParamPropCalc(long parId, long statAgrId) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CALC_AGR_FORMULA);
             PreparedStatement stmVars = connect.prepareStatement(SELECT_CALC_AGR_VARS)) {
            stm.setLong(1, parId);
            stm.setLong(2, statAgrId);

            ResultSet res = stm.executeQuery();
            if (res.next()) {
                stmVars.setLong(1, parId);
                stmVars.setLong(2, statAgrId);

                ParamPropCalc result = new ParamPropCalc(res.getString(1));

                ResultSet resVars = stmVars.executeQuery();
                while (resVars.next()) {
                    result.addParamProp(resVars.getString("variable"),
                            new ObjectParam(resVars.getInt("par_id"), resVars.getString("par_memo"), resVars.getString("par_name")),
                            new StatAggrTable(resVars.getInt("stat_agr_id"), resVars.getString("stat_agr_code")));
                }

                logger.log(Level.INFO, "Calculate parameter props: " + result);
                return result;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "getting Calc Agr formula error", e);
        }
        return new ParamPropCalc();
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
    public Long addParam(Long grandparentId, Long parentId, Param addParam, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_PARAM)) {
            cStm.setLong(1, grandparentId);
            cStm.setLong(2, parentId);
            cStm.setString(3, addParam.getParCode());
            cStm.setString(4, addParam.getParMemo());
            cStm.setString(5, addParam.getParName());
            cStm.setShort(6, addParam.getZone());
            if ((addParam.getTempStatus() == TemperatureStatus.GRAPH) && (addParam.getGraph() != null)) {
                cStm.setLong(7, addParam.getGraph().getId());
            } else {
                cStm.setNull(7, Types.BIGINT);
            }
            cStm.setLong(8, addParam.getVisible());
            if ((addParam.getTempStatus() == TemperatureStatus.DAILY_REDUCTION) && (addParam.getDecrease() != null)) {
                cStm.setLong(9, addParam.getDecrease().getId());
            } else {
                cStm.setNull(9, Types.BIGINT);
            }
            cStm.setShort(10, (short) (addParam.isEditEnable() ? 1 : 0));
            cStm.setShort(11, (short) (addParam.isLetoControl() ? 1 : 0));
            if ((addParam.getTempStatus() == TemperatureStatus.OPT_VALUE) && (addParam.getOptValue() != null)) {
                cStm.setBigDecimal(12, addParam.getOptValue());
            } else {
                cStm.setNull(12, Types.NUMERIC);
            }
            cStm.registerOutParameter(13, Types.BIGINT);
            cStm.setString(14, login);
            cStm.setString(15, ip);

            cStm.executeUpdate();

            logger.log(Level.INFO, "Add param {0}", addParam.getParName());
            return cStm.getLong(13);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error add param", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Добавление новой формулы
     *
     * @param login          имя пользователя
     * @param ip             адрес пользователя
     * @throws SystemParamException в случае ошибки создания новой единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addCalcAgrFormula(long par_id, long stat_agr_id, ParamPropCalc propCalc,
                                  String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_CALC_AGR_FORMULA)) {
            List<Object> dataList = new ArrayList<>();
            for (ParamPropCalc.PropRow propRow: propCalc.getProps()) {
                PGobject pGobject = new PGobject();
                pGobject.setType("dsp_0029t.vars_rec");

                StringJoiner value = new StringJoiner(",", "(", ")")
                        .add(propRow.getVariable())
                        .add(String.valueOf(propRow.getParam().getId()))
                        .add(String.valueOf(propRow.getStatAggr().getId()));

                pGobject.setValue(value.toString());
                dataList.add(pGobject);
            }
            Array array = connect.createArrayOf("dsp_0029t.vars_rec", dataList.toArray());
            cStm.setLong(1, par_id);
            cStm.setLong(2, stat_agr_id);
            cStm.setString(3, propCalc.getFormula());
            cStm.setArray(4, array);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            logger.info("add CalcAgrFormula " + propCalc.getFormula());

        } catch (SQLException e) {
            logger.log(Level.WARNING, "error add CalcAgrFormula", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Удаление формулы и расшифровки
     *
     * @param par_id      id параметра
     * @param stat_agr_id id свойства параметра
     * @param login       имя пользователя
     * @param ip          адрес пользователя
     * @throws SystemParamException в случае ошибки удаления единицы измерения
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeCalcAgrFormula(long par_id, long stat_agr_id, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement cStmCheck = connect.prepareStatement(SELECT_CHECK_LINKED_CALC_AGR);
             CallableStatement cStm = connect.prepareCall(FUN_DEL_CALC_AGR_FORMULA)) {
            cStmCheck.setLong(1, par_id);
            cStmCheck.setLong(2, stat_agr_id);

            ResultSet res = cStmCheck.executeQuery();
            if (res.next()) {
                if (res.getInt(1) != 0) {
                    logger.warning("linked ");

                    throw new SystemParamException("Вычислимый стат.агр. параметра слинкован");
                }

                cStm.setLong(1, par_id);
                cStm.setLong(2, stat_agr_id);
                cStm.setString(3, login);
                cStm.setString(4, ip);

                cStm.executeUpdate();

                logger.info("remove calcAgrFormula Par_id " + par_id +
                        " Stat_agr_id " + stat_agr_id);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error remove calcAgrFormula", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Удаление параметра
     *
     * @param selectedRowID id параметра
     * @param login         имя пользователя
     * @param ip            адрес пользователя
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

            logger.log(Level.INFO, "Remove param {0}", selectedRowID);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error remove Param", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
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
            cStm.setString(2, param.getParCode());
            cStm.setString(3, param.getParMemo());
            cStm.setString(4, param.getParName());
            cStm.setShort(5, param.getZone());
            if ((param.getTempStatus() == TemperatureStatus.GRAPH) && (param.getGraph() != null)) {
                cStm.setLong(6, param.getGraph().getId());
            } else {
                cStm.setNull(6, Types.BIGINT);
            }
            cStm.setLong(7, param.getVisible());
            if ((param.getTempStatus() == TemperatureStatus.DAILY_REDUCTION) && (param.getDecrease() != null)) {
                cStm.setLong(8, param.getDecrease().getId());
            } else {
                cStm.setNull(8, Types.BIGINT);
            }
            cStm.setShort(9, (short) (param.isEditEnable() ? 1 : 0));
            cStm.setShort(10, (short) (param.isLetoControl() ? 1 : 0));
            if ((param.getTempStatus() == TemperatureStatus.OPT_VALUE) && (param.getOptValue() != null)) {
                cStm.setBigDecimal(11, param.getOptValue());
            } else {
                cStm.setNull(11, Types.NUMERIC);
            }
            cStm.setString(12, login);
            cStm.setString(13, ip);

            cStm.executeUpdate();

            logger.log(Level.INFO, "update param {0}", param.getId());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error update Param", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
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
            cStm.setLong(1, paramProp.getParId());
            cStm.setLong(2, paramProp.getStatAgrId());
            cStm.setLong(3, paramProp.getPropId());
            cStm.setString(4, paramProp.getPropValDef());
            cStm.setLong(5, paramProp.getGreatCond().getId());
            cStm.setLong(6, paramProp.getLessCond().getId());
            cStm.setString(7, login);
            cStm.setString(8, ip);

            cStm.executeUpdate();

            logger.log(Level.INFO, "Update param prop id {0}", paramProp.getParId());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error update Param prop ", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
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
            cStm.setLong(1, paramPropPer.getParId());
            cStm.setLong(2, paramPropPer.getStatAgrId());
            cStm.setLong(3, paramPropPer.getEnumCode());
            cStm.setString(4, paramPropPer.getPropCond().getName());
            cStm.setLong(5, paramPropPer.getPropCond().getId());
            cStm.setString(6, login);
            cStm.setString(7, ip);

            cStm.executeUpdate();

            logger.log(Level.INFO, "Update param prop per {0}", paramPropPer.getParId());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error update Param prop per", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Обновление галки визуализации
     *
     * @param selectedRow параметр
     * @param login       имя пользователя
     * @param ip          адрес пользователя
     * @throws SystemParamException в случае ошибки обновления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updParamStat(Long parentId, GMTree selectedRow, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_PARAM_STAT)) {
            cStm.setLong(1, parentId);
            cStm.setLong(2, selectedRow.getMyId());
            cStm.setShort(3, selectedRow.getMeasureId().shortValue());
            cStm.setShort(4, (short) selectedRow.getVisStat());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            logger.log(Level.INFO, "update param stat {0}", selectedRow);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error update Param stat ", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    /**
     * Проверка правильности ввода формулы
     */
    public List<String> checkNewFormula(String formula) throws SystemParamException {
        List<String> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CHECK_NEW_FORMULA)) {
            stm.setString(1, formula);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add((res.getString("variable")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
        return result;
    }

    /**
     * Запрос списка для выбора параметра для формулы вычисления
     *
     * @return Список для выбора параметра для формулы вычисления
     */
    public List<ObjectParam> getParamList() {
        List<ObjectParam> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_LIST)) {

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new ObjectParam(res.getInt("id"),
                        res.getString("par_memo"),
                        res.getString("par_name")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    /**
     * Запрос списка для выбора стат.агрегата для параметра из формулы вычисления
     *
     * @return Список выбора стат.агрегата для параметра из формулы вычисления
     */
    public List<StatAggrTable> getStatAgrList(int par_id) {
        List<StatAggrTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STAT_AGR_LIST)) {
            stm.setLong(1, par_id);

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new StatAggrTable(res.getLong("stat_agr_id"),
                        res.getString("stat_agr_code")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }
}