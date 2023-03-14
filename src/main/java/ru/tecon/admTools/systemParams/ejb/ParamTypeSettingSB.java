package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.*;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для получения данных под форму Настройка типа параметра
 * @author Maksim Shchelkonogov
 * 17.02.2023
 */
@Stateless
@LocalBean
public class ParamTypeSettingSB {

    private static final String SEL_PARAM_TYPE_AND_AGGR = "select leftTable.param_type_name, leftTable.param_type_id, rightTable.stat_agr_id, " +
            "       rightTable.stat_agr_code, rightTable.measure_id, rightTable.measure_name, " +
            "       rightTable.gen_stat_agr, rightTable.categ, rightTable.stat_agr_name " +
            "    from sys_0001t.sel_param_type() leftTable " +
            "        left join (select * from sys_0001t.sel_param_type_stat(0)) rightTable " +
            "            on leftTable.param_type_id = rightTable.param_type_id " +
            "order by leftTable.param_type_name, rightTable.stat_agr_id";

    private static final String SET_GEN_STAT_AGGR = "call sys_0001t.set_gen_stat_agr(?, ?, ?, ?, ?)";

    private static final String SEL_PARAM_CONDITION = "select * from sys_0001t.sel_param_condition()";
    private static final String SEL_PARAM_PROP = "select * from sys_0001t.list_prop_for_param_type_stat(?, ?)";

    private static final String SEL_TYPE_PROP_A = "select * from sys_0001t.sel_param_type_stat_prop(?, ?)";
    private static final String ADD_TYPE_PROP_A = "call sys_0001t.add_param_type_stat_prop(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DEL_TYPE_PROP_A = "call sys_0001t.del_param_type_stat_prop(?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_TYPE_PROP_P = "select * from sys_0001t.sel_param_type_stat_prop_per(?, ?)";
    private static final String ADD_TYPE_PROP_P = "call sys_0001t.add_param_type_stat_prop_per(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DEL_TYPE_PROP_P = "call sys_0001t.del_param_type_stat_prop_per(?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_STAT_AGGREGATES = "select * from sys_0001t.list_stat_agr_for_param_type(?)";

    private static final String ADD_TYPE = "call sys_0001t.add_param_type(?, ?, ?, ?, ?)";
    private static final String ADD_AGGREGATE_TO_TYPE = "call sys_0001t.add_param_type_stat(?, ?, ?, ?, ?, ?, ?)";
    private static final String DEL_TYPE = "call sys_0001t.del_param_type(?, ?, ?, ?, ?)";
    private static final String DEL_TYPE_AGGREGATE = "call sys_0001t.del_param_type_stat(?, ?, ?, ?, ?, ?)";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получения списка данных для отображения типа параметра
     * @return список данных
     */
    public List<ParamTypeTable> getParamTypes() {
        List<ParamTypeTable> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_PARAM_TYPE_AND_AGGR)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                ParamType paramType = new ParamType(res.getInt("param_type_id"), res.getString("param_type_name"));

                StatisticalAggregate aggregate = null;
                if (res.getObject("stat_agr_id") != null) {
                    aggregate = new StatisticalAggregate(res.getInt("stat_agr_id"), res.getString("stat_agr_name"), res.getString("stat_agr_code"));
                }

                Measure measure = null;
                if (res.getObject("measure_id") != null) {
                    measure = new Measure();
                    measure.setId(res.getInt("measure_id"));
                    measure.setShortName(res.getString("measure_name"));
                }


                boolean mainAggregate = false;
                if (res.getObject("gen_stat_agr") != null) {
                    mainAggregate = res.getString("gen_stat_agr").toLowerCase().equals("y");
                }

                result.add(new ParamTypeTable(paramType, aggregate, measure, res.getString("categ"), mainAggregate));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load data from db for paramType", ex);
        }
        return result;
    }

    /**
     * Изменение главного агрегата у типа параметра
     * @param data тип параметра
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException если произошла ошибка записи данных в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeGenStat(ParamTypeTable data, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(SET_GEN_STAT_AGGR)) {
            cStm.setLong(1, data.getParamType().getId());
            cStm.setInt(2, data.getAggregate().getId());
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "Update generate stat for " + data + " result " + cStm.getShort(5));

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Невозможно изменить визуализацию");
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error update generate stat", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка данных для свойств типа параметра категории A
     * @param data тип параметра
     * @return список свойств
     * @throws SystemParamException в случае ошибки обращения к базе
     */
    public List<ParamPropTableA> getTypePropsA(ParamTypeTable data) throws SystemParamException {
        List<ParamPropTableA> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TYPE_PROP_A)) {
            stm.setInt(1, data.getParamType().getId());
            stm.setInt(2, data.getAggregate().getId());

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                Properties prop = new Properties(res.getInt("prop_id"), res.getString("prop_name"));
                Condition lessCond = new Condition(res.getInt("prop_cond_less"), res.getString("prop_cond_less_name"));
                Condition greatCond = new Condition(res.getInt("prop_cond_great"), res.getString("prop_cond_great_name"));
                result.add(new ParamPropTableA(prop, lessCond, greatCond));
            }
            return result;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load data from db for paramTypeProp A category", ex);
            throw new SystemParamException("Ошибка получения данных из базы");
        }
    }

    /**
     * Получение списка данных для свойств типа параметра категории P
     * @param data тип параметра
     * @return список свойств
     * @throws SystemParamException в случае ошибки обращения к базе
     */
    public List<ParamPropTableP> getTypePropsP(ParamTypeTable data) throws SystemParamException {
        List<ParamPropTableP> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TYPE_PROP_P)) {
            stm.setInt(1, data.getParamType().getId());
            stm.setInt(2, data.getAggregate().getId());

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                Condition cond = new Condition(res.getInt("prop_cond"), res.getString("prop_cond_name"));
                result.add(new ParamPropTableP(res.getInt("enum_code"), res.getString("prop_val"), cond));
            }
            return result;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load data from db for paramTypeProp P category", ex);
            throw new SystemParamException("Ошибка получения данных из базы");
        }
    }

    /**
     * Получение списка постояний параметров
     * @return список состояний
     */
    public List<Condition> getParamConditions() {
        List<Condition> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_PARAM_CONDITION)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Condition(res.getInt("param_cond_id"), res.getString("param_cond_name")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load parameters conditions", ex);
        }
        return result;
    }

    /**
     * Добавление свойства для типа параметра категории P
     * @param type тип параметра
     * @param prop новое свойство
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addPropP(ParamTypeTable type, ParamPropTableP prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_TYPE_PROP_P)) {
            cStm.setLong(1, type.getParamType().getId());
            cStm.setLong(2, type.getAggregate().getId());
            cStm.setLong(3, prop.getCode());
            cStm.setString(4, prop.getProp());
            cStm.setLong(5, prop.getCondition().getId());
            cStm.setString(6, login);
            cStm.setString(7, ip);
            cStm.registerOutParameter(8, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "add new propP {0} for {1} result {2}", new Object[]{prop, type, cStm.getShort(8)});

            if (cStm.getShort(8) != 0) {
                throw new SystemParamException("Ошибка добавления нового свойства");
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "error to add prop", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаления свойства типа параметра категории P
     * @param type тип параметра
     * @param prop свойство для удаления
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removePropP(ParamTypeTable type, ParamPropTableP prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(DEL_TYPE_PROP_P)) {
            cStm.setLong(1, type.getParamType().getId());
            cStm.setLong(2, type.getAggregate().getId());
            cStm.setLong(3, prop.getCode());
            cStm.registerOutParameter(4, Types.VARCHAR);
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "remove propP {0} in {1} result {2}", new Object[]{prop, type, cStm.getShort(7)});

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException(cStm.getString(4));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error remove property", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка доступных свойств для добавления к типу параметру категории A
     * @param type тип параметра
     * @return список свойств
     */
    public List<Properties> getParamProperties(ParamTypeTable type) {
        List<Properties> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_PARAM_PROP)) {
            stm.setLong(1, type.getParamType().getId());
            stm.setLong(2, type.getAggregate().getId());

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Properties(res.getInt("prop_code"), res.getString("prop_name")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load param properties", ex);
        }
        return result;
    }

    /**
     * Удаления свойства типа параметра категории A
     * @param type тип параметра
     * @param prop свойство для удаления
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removePropA(ParamTypeTable type, ParamPropTableA prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(DEL_TYPE_PROP_A)) {
            cStm.setLong(1, type.getParamType().getId());
            cStm.setLong(2, type.getAggregate().getId());
            cStm.setLong(3, prop.getProp().getId());
            cStm.registerOutParameter(4, Types.VARCHAR);
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "remove propA {0} in {1} result {2}", new Object[]{prop, type, cStm.getShort(7)});

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException(cStm.getString(4));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error remove property", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Добавление свойства для типа параметра категории A
     * @param type тип параметра
     * @param prop новое свойство
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addPropA(ParamTypeTable type, ParamPropTableA prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_TYPE_PROP_A)) {
            cStm.setLong(1, type.getParamType().getId());
            cStm.setLong(2, type.getAggregate().getId());
            cStm.setLong(3, prop.getProp().getId());
            cStm.setLong(4, prop.getGreatCond().getId());
            cStm.setLong(5, prop.getLessCond().getId());
            cStm.setString(6, login);
            cStm.setString(7, ip);
            cStm.registerOutParameter(8, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "add new propA {0} for {1} result {2}", new Object[]{prop, type, cStm.getShort(8)});

            if (cStm.getShort(8) != 0) {
                throw new SystemParamException("Ошибка добавления нового свойства");
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "error to add prop", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка доступных агрегатов для добавления к типу параметра
     * @param paramTypeID идентификатор типа параметра
     * @return список агрегатов
     */
    public List<StatisticalAggregate> getListStatAggregateForParamType(int paramTypeID) {
        List<StatisticalAggregate> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_STAT_AGGREGATES)) {
            stm.setInt(1, paramTypeID);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StatisticalAggregate(res.getInt("stat_agr_id"), res.getString("stat_agr_name"), res.getString("stat_agr_code")));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error load list statistical aggregates", ex);
        }
        return result;
    }

    /**
     * Создание нового типа параметра
     * @param type новый тип
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки создания нового типа
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addType(ParamType type, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_TYPE)) {
            cStm.setString(1, type.getName());
            cStm.registerOutParameter(2, Types.BIGINT);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "add new type {0} result {1}", new Object[]{type, cStm.getShort(5)});

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Ошибка создания типа " + type.getName());
            }

            type.setId((int) cStm.getLong(2));
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "error to add type", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Добавление нового агрегата к типу параметра
     * @param typeTable новый агрегат
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в слчае ошибки добавления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addAggregateToType(ParamTypeTable typeTable, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_AGGREGATE_TO_TYPE)) {
            cStm.setLong(1, typeTable.getParamType().getId());
            cStm.setInt(2, typeTable.getAggregate().getId());
            cStm.setShort(3, typeTable.getMeasure().getId().shortValue());
            cStm.setString(4, typeTable.getCategory());
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "add aggregate to type {0} result {1}", new Object[]{typeTable, cStm.getShort(7)});

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка добавления статистического агрегата " + typeTable.getAggregate().getName() +
                        " к типу " + typeTable.getParamType().getName());
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "error to add aggregate to type", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление типа параметра
     * @param paramType тип параметра
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeType(ParamTypeTable paramType, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(DEL_TYPE)) {
            cStm.setLong(1, paramType.getParamType().getId());
            cStm.registerOutParameter(2, Types.VARCHAR);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "remove type {0} result {1}", new Object[]{paramType, cStm.getShort(5)});

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Ошибка удаления типа " + cStm.getString(2));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error remove type", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление агрегата типа параметра
     * @param paramType агрегат для удаления
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeTypeAggregate(ParamTypeTable paramType, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(DEL_TYPE_AGGREGATE)) {
            cStm.setLong(1, paramType.getParamType().getId());
            cStm.setLong(2, paramType.getAggregate().getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            logger.log(Level.INFO, "remove type aggregate {0} result {1}", new Object[]{paramType, cStm.getShort(6)});

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка удаления агрегата " + cStm.getString(3));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error remove type aggregate", ex);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
