package ru.tecon.admTools.mobile;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.mobile.model.AsyncData;
import ru.tecon.admTools.mobile.model.ObjectData;
import ru.tecon.admTools.mobile.model.UserObject;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 21.04.2025
 */
@Named
@Stateless(name = "techParamBean", mappedName = "ejb/techParamBean")
@Local(TechParamLocal.class)
public class TechParamSB implements TechParamLocal {

    private static final String SEL_REDIRECT = "select * from m_adm.get_td_application_url(?)";
    private static final String SEL_USER_OBJECTS = "select * from admin.sel_ctp_list(545, 0, '', ?)";
    private static final String SEL_OBJ_PARAMS = "select par_id, stat_aggr, par_memo from dsp_0032t.get_obj_params(?) order by visible";
    private static final String SEL_MAIN_OBJ_PARAMS = "select par_id, stat_aggr, par_memo from dsp_0032t.get_obj_params(?) " +
            "where par_id in (select par_id from admin.dz_basic_param where obj_type_id = 1) order by visible";
    private static final String SEL_PARAM_DATA = "select par_value, color from dsp_0032t.get_data_param(?, ?, ?, now()::date)";
    private static final String SEL_TNV = "select tnv, color from dsp_0032t.get_tnv(?, now()::date)";
    private static final String CALL_ASYNC_DATA = "call dsp_0032t.call_async_refresh(?, ?, ?, ?)";
    private static final String CHECK_ASYNC_DATA = "select * from dsp_0032t.check_async_refresh_status(?)";
    private static final String SEL_ASYNC_DATA = "select par_memo, time_stamp, par_value, param_cond_name, color from dsp_0032t.sel_async_refresh_data(?)";

    @Inject
    private Logger logger;

    @EJB(name = "techParamBean")
    private TechParamLocal self;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение url для перехода
     *
     * @param name имя свойства для перехода
     * @return url
     */
    @Override
    public String getRedirectUrl(String name) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_REDIRECT)) {
            stm.setString(1, name);

            ResultSet res = stm.executeQuery();
            if (res.next()) {
               return res.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load catalog types", e);
        }
        return "";
    }

    /**
     * Получение объектов пользователя
     *
     * @param user идентификатор пользователя
     * @return объекты пользователя
     */
    @Override
    public List<UserObject> getUserObjects(String user) {
        List<UserObject> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_USER_OBJECTS)) {
            stm.setFetchSize(1000);
            stm.setString(1, user);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new UserObject(res.getInt("obj_id"), res.getString("obj_name")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load user objects", e);
        }
        return result;
    }

    /**
     * Получение данных по объекту
     *
     * @param objectId идентификатор объекта
     * @return данные объекта
     */
    @Override
    public List<ObjectData> getObjectData(int objectId, boolean allData) {
        List<ObjectData> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stmAll = connect.prepareStatement(SEL_OBJ_PARAMS);
             PreparedStatement stmMain = connect.prepareStatement(SEL_MAIN_OBJ_PARAMS)) {
            List<Future<?>> futures = new ArrayList<>();

            ObjectData tnvData = new ObjectData("Тгмц");
            futures.add(self.getTnvData(tnvData, objectId));
            result.add(tnvData);

            ResultSet res;

            if (allData) {
                stmAll.setInt(1, objectId);
                res = stmAll.executeQuery();
            } else {
                stmMain.setInt(1, objectId);
                res = stmMain.executeQuery();
            }

            while (res.next()) {
                ObjectData objectData = new ObjectData(res.getString("par_memo"));
                futures.add(self.getParamData(objectData, objectId, res.getInt("par_id"), res.getInt("stat_aggr")));
                result.add(objectData);
            }

            for (Future<?> future: futures) {
                future.get();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load object data", e);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Получение данных по параметрам
     *
     * @param objectData информация по объекту
     * @param objectId идентификатор объекта
     * @param parId идентификатор параметра
     * @param statAggr идентификатор агрегата
     * @return null
     */
    @Asynchronous
    @Override
    public Future<Void> getParamData(ObjectData objectData, int objectId, int parId, int statAggr) {
        logger.log(Level.INFO, "load param {0}", parId);
        List<ObjectData.Value> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_PARAM_DATA)) {
            stm.setInt(1, objectId);
            stm.setInt(2, parId);
            stm.setInt(3, statAggr);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                String parValue = res.getString("par_value");
                if (parValue != null) {
                    try {
                        parValue = new BigDecimal(parValue).setScale(2, RoundingMode.DOWN).toString();
                    } catch (NumberFormatException ignore) {
                    }
                }
                result.add(new ObjectData.Value(parValue, res.getString("color")));
            }
            for (int i = result.size(); i < 24; i++) {
                result.add(new ObjectData.Value("", ""));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load param data", e);
        }
        objectData.setValues(result);
        logger.log(Level.INFO, "finish load param {0}", parId);
        return new AsyncResult<>(null);
    }

    /**
     * Получение данных по температуре наружного воздуха
     *
     * @param objectData информация по объекту
     * @param objectId идентификатор объекта
     * @return null
     */
    @Asynchronous
    @Override
    public Future<Void> getTnvData(ObjectData objectData, int objectId) {
        logger.log(Level.INFO, "load tnv");
        List<ObjectData.Value> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TNV)) {
            stm.setInt(1, objectId);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                String parValue = res.getString("tnv");
                if (parValue != null) {
                    try {
                        parValue = new BigDecimal(parValue).setScale(2, RoundingMode.DOWN).toString();
                    } catch (NumberFormatException ignore) {
                    }
                }
                result.add(new ObjectData.Value(parValue, res.getString("color")));
            }
            for (int i = result.size(); i < 24; i++) {
                result.add(new ObjectData.Value("", ""));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load tnv", e);
        }
        objectData.setValues(result);
        logger.log(Level.INFO, "finish load tnv");
        return new AsyncResult<>(null);
    }

    /**
     * Получение запроса на мгновенные данные
     *
     * @param objectId идентификатор объекта
     * @param userName идентификатор пользователя
     * @return идентификатор мгновенного запроса
     */
    @Override
    public String getAsyncRequest(int objectId, String userName) {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(CALL_ASYNC_DATA)) {
            cStm.setInt(1, objectId);
            cStm.setString(2, userName);
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.registerOutParameter(4, Types.INTEGER);

            cStm.executeUpdate();

            if (cStm.getInt(4) == 0) {
                return cStm.getString(3);
            } else {
                logger.log(Level.WARNING, "error load request async data");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load async data", e);
        }
        return "";
    }

    /**
     * Получение данных по мгновенному запросу
     *
     * @param AsyncRequest идентификатор мгновенного запроса
     * @return данные по мгновенному запросу
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AsyncData> getAsyncData(String AsyncRequest) {
        List<AsyncData> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stmCheck = connect.prepareStatement(CHECK_ASYNC_DATA);
             PreparedStatement stmGet = connect.prepareStatement(SEL_ASYNC_DATA)) {
            for (int i = 0; i < 30; i++) {
                stmCheck.setString(1, AsyncRequest);

                System.out.println(AsyncRequest);

                ResultSet res = stmCheck.executeQuery();
                if (res.next() && (res.getInt(1) == 0)) {
                    stmGet.setString(1, AsyncRequest);

                    ResultSet resData = stmGet.executeQuery();
                    while (resData.next()) {
                        String parValue = resData.getString("par_value");
                        if (parValue != null) {
                            try {
                                parValue = new BigDecimal(parValue).setScale(2, RoundingMode.DOWN).toString();
                            } catch (NumberFormatException ignore) {
                            }
                        }
                        result.add(new AsyncData(resData.getString("par_memo"),
                                resData.getString("time_stamp"),
                                parValue,
                                resData.getString("param_cond_name"),
                                resData.getString("color")));
                    }
                    break;
                } else {
                    try {
                        Thread.sleep(6_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load async data", e);
        }
        return result;
    }
}
