package ru.tecon.admTools;

import ru.tecon.admTools.model.Condition;
import ru.tecon.admTools.model.DataModel;
import ru.tecon.admTools.model.GraphDecreaseItemModel;
import ru.tecon.admTools.model.ParamConditionDataModel;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Stateless
public class AdmToolsSB {

    private static final Logger LOG = Logger.getLogger(AdmToolsSB.class.getName());

    private static final String ALTER_SESSION = "alter session set NLS_NUMERIC_CHARACTERS = '.,'";

    private static final String SELECT_DATA = "select * from table(dsp_0031t.sel_a_params(?))";
    private static final String SELECT_GET_OBJECT_PATH = "select get_obj_path_all(?) || ' (' || get_obj_address(?) || ')' from dual";
    private static final String SELECT_ENUMERABLE_DATA = "select * from table(dsp_0031t.sel_p_params(?))";
    private static final String SELECT_PARAM_CONDITION = "select * from table(dsp_0031t.sel_p_param_cond(?, ?, ?))";
    private static final String SELECT_CONDITIONS = "select * from dz_param_condition order by param_cond_name";
    private static final String SELECT_DECREASES = "select * from table(dsp_0031t.sel_decrease_list()) order by code";
    private static final String SELECT_GRAPHS = "select * from table(dsp_0031t.sel_graph_list()) order by code";

    private static final String SAVE_GRAPH = "{? = call dsp_0031t.save_graph_rt(?, ?, ?, ?, ?, ?)}";
    private static final String SAVE_ENUM_PARAMS = "{? = call dsp_0031t.save_p_param(?, ?, ?, ?, ?, ?)}";
    private static final String SAVE_RANGES = "{? = call dsp_0031t.save_ranges(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    public List<DataModel> getData(int objectID) {
        List<DataModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement alter = connect.prepareStatement(ALTER_SESSION);
             PreparedStatement stm = connect.prepareStatement(SELECT_DATA)) {
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
                if (res.getString("decrease_id") != null) {
                    item.setDecreaseID(res.getInt("decrease_id"));
                }
                item.setDecreaseName(res.getString("decrease_name"));
                if (res.getString("graph_id") != null) {
                    item.setGraphID(res.getInt("graph_id"));
                }
                item.setGraphName(res.getString("graph_name"));
                item.setAbsolute(res.getBoolean("absolute"));
                if (res.getString("t_min_tech") != null) {
                    item.settMin(res.getDouble("t_min_tech"));
                }
                if (res.getString("t_max_tech") != null) {
                    item.settMax(res.getDouble("t_max_tech"));
                }
                if (res.getString("a_min_tech") != null) {
                    item.setaMin(res.getDouble("a_min_tech"));
                }
                if (res.getString("a_max_tech") != null) {
                    item.setaMax(res.getDouble("a_max_tech"));
                }
                item.settPercent(res.getBoolean("t_percent"));
                item.setaPercent(res.getBoolean("a_percent"));
                item.setaMaxDisable(!res.getBoolean("a_max_tech_edit"));
                item.setaMinDisable(!res.getBoolean("a_min_tech_edit"));
                item.settMaxDisable(!res.getBoolean("t_max_tech_edit"));
                item.settMinDisable(!res.getBoolean("t_min_tech_edit"));
                item.setColor(res.getBoolean("color"));
                item.setGraphDisable(!res.getBoolean("graph_edit"));
                item.setDecreaseDisable(!res.getBoolean("decrease_edit"));
                result.add(item);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load data", e);
        }
        return result;
    }

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
                result.add(item);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load enumerable data", e);
        }
        return result;
    }

    public List<ParamConditionDataModel> getParamCondition(int objectID, int parId, int statAgrID) {
        List<ParamConditionDataModel> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_PARAM_CONDITION)) {
            stm.setInt(1, objectID);
            stm.setInt(2, parId);
            stm.setInt(3, statAgrID);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ParamConditionDataModel(res.getInt("enum_code"), res.getString("prop_val"),
                        res.getString("prop_val_tech"), res.getInt("param_cond_id")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load param condition", e);
        }
        return result;
    }

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

    public List<GraphDecreaseItemModel> getDecreases() {
        return getGraphsDecreases(SELECT_DECREASES);
    }

    public List<GraphDecreaseItemModel> getGraphs() {
        return getGraphsDecreases(SELECT_GRAPHS);
    }

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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String saveGraph(int objectID, DataModel saveData) {
        if (saveData.isGraphDisable()) {
            return null;
        }
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_GRAPH)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, saveData.getParID());
            stm.setInt(4, saveData.getStatAgr());
            if (saveData.getGraphID() == null) {
                stm.setNull(5, Types.INTEGER);
                stm.setInt(6, Integer.parseInt(saveData.getGraphName()));
            } else {
                stm.setInt(5, saveData.getGraphID());
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveEnumParam(int objectID, DataModel saveData) {
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_ENUM_PARAMS)) {

            for (ParamConditionDataModel item: saveData.getParamConditions()) {
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

    public String saveRanges(int objectID, DataModel saveData) {
        try (Connection connect = ds.getConnection();
             CallableStatement stm = connect.prepareCall(SAVE_RANGES)) {
            stm.registerOutParameter(1, Types.INTEGER);
            stm.setInt(2, objectID);
            stm.setInt(3, saveData.getParID());
            stm.setInt(4, saveData.getStatAgr());

            if (saveData.getaMin() == null) {
                stm.setNull(5, Types.INTEGER);
            } else {
                stm.setDouble(5, saveData.getaMin());
            }
            if (saveData.gettMin() == null) {
                stm.setNull(6, Types.INTEGER);
            } else {
                stm.setDouble(6, saveData.gettMin());
            }
            if (saveData.gettMax() == null) {
                stm.setNull(7, Types.INTEGER);
            } else {
                stm.setDouble(7, saveData.gettMax());
            }
            if (saveData.getaMax() == null) {
                stm.setNull(8, Types.INTEGER);
            } else {
                stm.setDouble(8, saveData.getaMax());
            }


            System.out.println(saveData.istPercent() + " " + saveData.isaPercent() + " " + saveData.isAbsolute());

            stm.setInt(9, saveData.istPercent() ? 1 : 0);
            stm.setInt(10, saveData.isaPercent() ? 1 : 0);
            stm.setInt(11, saveData.isAbsolute() ? 0 : 1);

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
}
