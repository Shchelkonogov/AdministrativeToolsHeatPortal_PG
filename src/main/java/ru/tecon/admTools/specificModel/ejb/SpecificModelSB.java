package ru.tecon.admTools.specificModel.ejb;

import ru.tecon.admTools.specificModel.model.*;
import ru.tecon.admTools.specificModel.model.additionalModel.AnalogData;
import ru.tecon.admTools.specificModel.model.additionalModel.EnumerateData;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Stateless
@Local(SpecificModelLocal.class)
public class SpecificModelSB implements SpecificModelLocal {

    private static final Logger LOG = Logger.getLogger(SpecificModelSB.class.getName());

    private static final String ALTER_SESSION = "alter session set NLS_NUMERIC_CHARACTERS = '.,'";

    private static final String SELECT_DATA = "select * from table(dsp_0031t.sel_a_params(?))";
    private static final String SELECT_ECO_DATA = "select * from table(dsp_0050t.sel_a_params(?))";
    private static final String SELECT_GET_OBJECT_PATH = "select get_obj_path_all(?) || ' (' || get_obj_address(?) || ')' from dual";
    private static final String SELECT_ENUMERABLE_DATA = "select * from table(dsp_0031t.sel_p_params(?))";
    private static final String SELECT_PARAM_CONDITION = "select * from table(dsp_0031t.sel_p_param_cond(?, ?, ?))";
    private static final String SELECT_CONDITIONS = "select * from dz_param_condition order by param_cond_name";
    private static final String SELECT_DECREASES = "select * from table(dsp_0031t.sel_decrease_list()) order by code";
    private static final String SELECT_GRAPHS = "select * from table(dsp_0031t.sel_graph_list()) order by code";
    private static final String SELECT_GRAPH_DESCRIPTION = "select * from table(dsp_0031t.sel_graph_value(?))";
    private static final String SELECT_DECREASE_DESCRIPTION = "select * from table(dsp_0031t.sel_decrease_value(?))";
    private static final String SELECT_HISTORY = "select to_char(system_date, 'dd.mm.yyyy hh24:mi:ss') as system_date, " +
            "user_name, prop_name, old_val, new_val from table(dsp_0031t.sel_change_param_ranges(?, ?, ?))";

    private static final String SAVE_GRAPH = "{? = call dsp_0031t.save_graph_rt(?, ?, ?, ?, ?, ?)}";
    private static final String SAVE_ENUM_PARAMS = "{? = call dsp_0031t.save_p_param(?, ?, ?, ?, ?, ?)}";
    private static final String SAVE_RANGES = "{? = call dsp_0031t.save_ranges(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String SAVE_DECREASE = "{? = call dsp_0031t.save_decrease(?, ?, ?)}";
    private static final String CLEAR_RANGES = "{? = call dsp_0031t.clear_ranges(?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @Override
    public List<DataModel> getData(int objectID) {
        return getData(objectID, false);
    }

    @Override
    public List<DataModel> getData(int objectID, boolean eco) {
        List<DataModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement alter = connect.prepareStatement(ALTER_SESSION);
             PreparedStatement stm = connect.prepareStatement(eco ? SELECT_ECO_DATA : SELECT_DATA)) {
            alter.executeQuery();

            stm.setInt(1, objectID);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                DataModel item = new DataModel(res.getInt("par_id"), res.getInt("stat_agr_id"));

                item.setParName(res.getString("par_name"));
                item.setParMemo(res.getString("par_memo"));
                item.setStatAgrName(res.getString("stat_agr_name"));
                item.setZone(res.getString("zone"));
                item.setTechProcCode(res.getString("techproc_type_code"));
                item.setMeasureName(res.getString("measure_name"));
                item.setParamTypeName(res.getString("param_type_name"));

                AnalogData additionalData = new AnalogData();
                additionalData.setGraph(res.getString("graph_id") == null ? null : res.getInt("graph_id"),
                        res.getString("graph_name"),
                        !res.getBoolean("graph_edit"));
                additionalData.setDecrease(res.getString("decrease_id") == null ? null : res.getInt("decrease_id"),
                        res.getString("decrease_name"),
                        !res.getBoolean("decrease_edit"));
                additionalData.setT(res.getString("t_min_tech") == null ? null : res.getDouble("t_min_tech"),
                        res.getString("t_max_tech") == null ? null : res.getDouble("t_max_tech"),
                        !res.getBoolean("t_min_tech_edit"),
                        !res.getBoolean("t_max_tech_edit"),
                        res.getBoolean("t_percent"));
                additionalData.setA(res.getString("a_min_tech") == null ? null : res.getDouble("a_min_tech"),
                        res.getString("a_max_tech") == null ? null : res.getDouble("a_max_tech"),
                        !res.getBoolean("a_min_tech_edit"),
                        !res.getBoolean("a_max_tech_edit"),
                        res.getBoolean("a_percent"));
                additionalData.setAbsolute(res.getBoolean("absolute"));
                additionalData.setColor(res.getBoolean("color"));

                item.setAdditionalData(additionalData);

                result.add(item);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load analog data", e);
        }
        return result;
    }

    @Override
    public String getObjectPath(int objectID) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_GET_OBJECT_PATH)) {
            stm.setInt(1, objectID);
            stm.setInt(2, objectID);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getString(1);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load object path", e);
        }

        return "";
    }

    @Override
    public List<DataModel> getEnumerableData(int objectID) {
        List<DataModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_ENUMERABLE_DATA)) {
            stm.setInt(1, objectID);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                DataModel item = new DataModel(res.getInt("par_id"), res.getInt("stat_agr_id"));

                item.setParMemo(res.getString("par_memo"));
                item.setStatAgrName(res.getString("stat_agr_name"));
                item.setZone(res.getString("zone"));
                item.setTechProcCode(res.getString("techproc_type_code"));
                item.setParName(res.getString("par_name"));
                item.setParamTypeName(res.getString("param_type_name"));

                result.add(item);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load enumerable data", e);
        }
        return result;
    }

    @Override
    public EnumerateData getParamCondition(int objectID, int parId, int statAgrID) {
        EnumerateData result = new EnumerateData();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_CONDITION)) {
            stm.setInt(1, objectID);
            stm.setInt(2, parId);
            stm.setInt(3, statAgrID);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.addCondition(res.getInt("enum_code"),
                        res.getString("prop_val"),
                        res.getInt("param_cond_id"),
                        res.getString("prop_val_tech"));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load param condition", e);
        }
        return result;
    }

    @Override
    public List<Condition> getConditions() {
        List<Condition> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_CONDITIONS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Condition(res.getInt(1), res.getString(2)));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load conditions", e);
        }
        return result;
    }

    @Override
    public List<GraphDecreaseItemModel> getDecreases() {
        return getGraphsDecreases(SELECT_DECREASES);
    }

    @Override
    public List<GraphDecreaseItemModel> getGraphs() {
        return getGraphsDecreases(SELECT_GRAPHS);
    }

    /**
     * Метод обертка, выполняет запрос на получения списка графиков или снижений
     * @param sql запрос
     * @return полученный список
     */
    private List<GraphDecreaseItemModel> getGraphsDecreases(String sql) {
        List<GraphDecreaseItemModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(sql)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new GraphDecreaseItemModel(res.getInt(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load graph/decreases list", e);
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String saveGraph(int objectID, DataModel saveData) {
        AnalogData data = (AnalogData) saveData.getAdditionalData();
        if (data.isGraphDisable()) {
            return null;
        }
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_GRAPH)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, saveData.getParID());
            stm.setInt(4, saveData.getStatAgr());
            if (data.getGraphID() == null) {
                if ((data.getGraphName() != null) && !data.getGraphName().isEmpty()) {
                    stm.setNull(5, Types.INTEGER);
                    stm.setFloat(6, Float.parseFloat(data.getGraphName()));
                } else {
                    stm.setNull(5, Types.INTEGER);
                    stm.setNull(6, Types.INTEGER);
                }
            } else {
                stm.setInt(5, data.getGraphID());
                stm.setNull(6, Types.INTEGER);
            }
            stm.registerOutParameter(7, Types.VARCHAR);
            stm.executeUpdate();

            if (stm.getInt(1) == 1) {
                return stm.getString(7);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error upload graph data", e);
        }

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveEnumParam(int objectID, DataModel saveData) {
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_ENUM_PARAMS)) {

            for (EnumerateData.ParamCondition item: ((EnumerateData) saveData.getAdditionalData()).getConditions()) {
                if (item.isEdited()) {
                    try {
                        stm.registerOutParameter(1, Types.INTEGER);
                        stm.setInt(2, objectID);
                        stm.setInt(3, saveData.getParID());
                        stm.setInt(4, saveData.getStatAgr());
                        stm.setInt(5, item.getEnumCode());
                        stm.setString(6, item.getCondition().getName());
                        stm.setInt(7, item.getCondition().getId());
                        stm.executeUpdate();
                    } catch (SQLException e) {
                        LOG.log(Level.WARNING, "error upload enum param", e);
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error upload enum param", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String saveRanges(int objectID, DataModel saveData) {
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_RANGES)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, saveData.getParID());
            stm.setInt(4, saveData.getStatAgr());

            AnalogData data = (AnalogData) saveData.getAdditionalData();

            if (data.getaMin() == null) {
                stm.setNull(5, Types.INTEGER);
            } else {
                stm.setDouble(5, data.getaMin());
            }
            if (data.gettMin() == null) {
                stm.setNull(6, Types.INTEGER);
            } else {
                stm.setDouble(6, data.gettMin());
            }
            if (data.gettMax() == null) {
                stm.setNull(7, Types.INTEGER);
            } else {
                stm.setDouble(7, data.gettMax());
            }
            if (data.getaMax() == null) {
                stm.setNull(8, Types.INTEGER);
            } else {
                stm.setDouble(8, data.getaMax());
            }

            stm.setInt(9, data.istPercent() ? 1 : 0);
            stm.setInt(10, data.isaPercent() ? 1 : 0);
            stm.setInt(11, data.isAbsolute() ? 0 : 1);

            stm.registerOutParameter(12, Types.VARCHAR);
            stm.executeUpdate();

            if (stm.getInt(1) == 1) {
                return stm.getString(12);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error upload ranges", e);
        }

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecrease(int objectID, DataModel saveData) {
        AnalogData data = (AnalogData) saveData.getAdditionalData();
        if (data.isDecreaseDisable()) {
            return;
        }
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_DECREASE)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, saveData.getParID());
            if (data.getDecreaseID() == null) {
                stm.setNull(4, Types.INTEGER);
            } else {
                stm.setInt(4, data.getDecreaseID());
            }
            stm.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error upload decrease", e);
        }
    }

    @Override
    public List<GraphDecreaseDescription> getGraphDescription(int graphID) {
        return getGraphDecreaseDescriptions(graphID, SELECT_GRAPH_DESCRIPTION);
    }

    @Override
    public List<GraphDecreaseDescription> getDecreaseDescription(int decreaseID) {
        return getGraphDecreaseDescriptions(decreaseID, SELECT_DECREASE_DESCRIPTION);
    }

    /**
     * Метод обертка, выполняет запрос на получения списка графиков или снижений
     * @param id id графика или суточного снижения
     * @param selectDecreaseDescription select
     * @return полученный список
     */
    private List<GraphDecreaseDescription> getGraphDecreaseDescriptions(int id, String selectDecreaseDescription) {
        List<GraphDecreaseDescription> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(selectDecreaseDescription)) {
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new GraphDecreaseDescription(res.getString("x"), res.getString("y")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load graph/decrease description", e);
        }
        return result;
    }

    @Override
    public List<ParamHistory> getParamHistory(int objectID, int paramID, int statAgrID) {
        List<ParamHistory> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_HISTORY)) {
            stm.setInt(1, objectID);
            stm.setInt(2, paramID);
            stm.setInt(3, statAgrID);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ParamHistory(res.getString("system_date"),
                        res.getString("user_name"),
                        res.getString("prop_name"),
                        res.getString("old_val"),
                        res.getString("new_val")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load param history", e);
        }
        return result;
    }

    @Override
    public void clearRanges(int objectID, int parID, int statAgrID) {
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(CLEAR_RANGES)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, parID);
            stm.setInt(4, statAgrID);
            stm.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error clear ranges", e);
        }
    }
}
