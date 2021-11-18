package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.normIndicators.*;

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
 * Stateless bean для загрузки данных по форме нормативные показатели
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class NormIndicatorsSB {

    private static final Logger LOG = Logger.getLogger(NormIndicatorsSB.class.getName());

    private static final String SEL_TV = "select * from table(sys_0001t.sel_norm_ind_tv())";
    private static final String FUN_UPD_TV = "{? = call sys_0001t.upd_norm_ind_tv(?, ?, ?, ?, ?)}";

    private static final String SEL_CO = "select * from table(sys_0001t.sel_norm_ind_co(?))";
    private static final String FUN_UPD_CO = "{? = call sys_0001t.upd_norm_ind_co(?, ?, ?, ?, ?)}";

    private static final String SEL_GVS = "select * from table(sys_0001t.sel_norm_ind_gvs(?))";
    private static final String FUN_UPD_GVS = "{? = call sys_0001t.upd_norm_ind_gvs(?, ?, ?, ?)}";

    private static final String SEL_VENT = "select * from table(sys_0001t.sel_norm_ind_vent(?))";
    private static final String FUN_UPD_VENT = "{? = call sys_0001t.upd_norm_ind_vent(?, ?, ?, ?, ?)}";

    private static final String SEL_OTHER = "select * from table(sys_0001t.sel_norm_ind_other())";
    private static final String FUN_UPD_OTHER = "{? = call sys_0001t.upd_norm_ind_other(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка нормативных показателей теплового ввода
     * @return список данных
     */
    public List<IndicatorTV> getTV() {
        List<IndicatorTV> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_TV)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new IndicatorTV(res.getDouble("tv_kpn"), res.getDouble("tv_kz"), res.getDouble("tv_kp")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load TV indicators", e);
        }
        return result;
    }

    /**
     * Запись в базу изменений нормативных показателей теплового ввода
     * @param indicatorTV измененные нормативные показатели теплового ввода
     * @param login идентификатор пользователя
     * @param ip адрес
     * @throws SystemParamException ошибка записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateTV(IndicatorTV indicatorTV, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_TV)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDouble(2, indicatorTV.getKpn());
            cStm.setDouble(3, indicatorTV.getKz());
            cStm.setDouble(4, indicatorTV.getKp());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOG.info("update norm indicator TV " + indicatorTV + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления показателя теплового ввода");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка нормативных показателей центрального отопления
     * @return список данных
     */
    public List<IndicatorCO> getCO() {
        List<IndicatorCO> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_CO)) {
            stm.setInt(1, 1);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorCO(1, "Источник", res.getDouble("co_kz"), res.getDouble("co_kp")));
            }

            stm.setInt(1, 303);
            res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorCO(303, "Потребитель", res.getDouble("co_kz"), res.getDouble("co_kp")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load CO indicator", e);
        }
        return result;
    }

    /**
     * Запись в базу изменений нормативных показателей центрального отопления
     * @param indicatorCO измененные нормативные показатели центрального отопления
     * @param login идентификатор пользователя
     * @param ip адрес
     * @throws SystemParamException ошибка записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCO(IndicatorCO indicatorCO, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_CO)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDouble(2, indicatorCO.getId());
            cStm.setDouble(3, indicatorCO.getKz());
            cStm.setDouble(4, indicatorCO.getKp());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOG.info("update norm indicator CO " + indicatorCO + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления показателя центрального отопления");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка нормативных показателей горячего водоснабжения
     * @return список данных
     */
    public List<IndicatorGVS> getGVS() {
        List<IndicatorGVS> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_GVS)) {
            stm.setInt(1, 1);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorGVS(1, "Источник", res.getDouble("gvs_kc")));
            }

            stm.setInt(1, 303);
            res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorGVS(303, "Потребитель", res.getDouble("gvs_kc")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load GVS indicator", e);
        }
        return result;
    }

    /**
     * Запись в базу изменений нормативных показателей горячего водоснабжения
     * @param indicatorGVS измененные нормативные показатели горячего водоснабжения
     * @param login идентификатор пользователя
     * @param ip адрес
     * @throws SystemParamException ошибка записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateGVS(IndicatorGVS indicatorGVS, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_GVS)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDouble(2, indicatorGVS.getId());
            cStm.setDouble(3, indicatorGVS.getKc());
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            LOG.info("update norm indicator GVS " + indicatorGVS + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления показателя горячего водоснабжения");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка нормативных показателей винтиляции
     * @return список данных
     */
    public List<IndicatorVENT> getVENT() {
        List<IndicatorVENT> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_VENT)) {
            stm.setInt(1, 1);
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorVENT(1, "Источник", res.getDouble("vent_kz"), res.getDouble("vent_kp")));
            }

            stm.setInt(1, 303);
            res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorVENT(303, "Потребитель", res.getDouble("vent_kz"), res.getDouble("vent_kp")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load VENT indicator", e);
        }
        return result;
    }

    /**
     * Запись в базу данных изменений нормативных показателей вентиляции
     * @param indicatorVENT измененные нормативные показатели вентиляции
     * @param login идентификации пользователя
     * @param ip адрес
     * @throws SystemParamException ошибка записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateVENT(IndicatorVENT indicatorVENT, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_VENT)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDouble(2, indicatorVENT.getId());
            cStm.setDouble(3, indicatorVENT.getKz());
            cStm.setDouble(4, indicatorVENT.getKp());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOG.info("update norm indicator VENT " + indicatorVENT + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления показателя вентиляции");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка общих нормативных показателей
     * @return список данных
     */
    public List<IndicatorOther> getOther() {
        List<IndicatorOther> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_OTHER)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorOther(res.getDouble("gvs_t7"), res.getDouble("gvs_dt7"), res.getDouble("kt"), res.getDouble("kto"),
                        res.getDouble("kg"), res.getDouble("kv"), res.getDouble("co_ku"), res.getDouble("vent_ku"), res.getDouble("gvs_ku"),
                        res.getDouble("kpco"), res.getDouble("kdgco"), res.getDouble("k1_min"), res.getDouble("k1_max"), res.getDouble("k2_min"),
                        res.getDouble("k2_max"), res.getDouble("kpgvs"), res.getDouble("kdggvs"), res.getDouble("kqgvsl"), res.getDouble("gvs_dt"),
                        res.getDouble("kgvsc")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load VENT indicator", e);
        }
        return result;
    }

    /**
     * Запись в базу данных изменений общих нормативных показателей
     * @param indicatorOther измененные общие нормативные показатели
     * @param login идентификатор пользователя
     * @param ip адрес
     * @throws SystemParamException ошибка записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateOther(IndicatorOther indicatorOther, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_OTHER)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setDouble(2, indicatorOther.getT7());
            cStm.setDouble(3, indicatorOther.getDt7());
            cStm.setDouble(4, indicatorOther.getKt());
            cStm.setDouble(5, indicatorOther.getKto());
            cStm.setDouble(6, indicatorOther.getKg());
            cStm.setDouble(7, indicatorOther.getKv());
            cStm.setDouble(8, indicatorOther.getKuco());
            cStm.setDouble(9, indicatorOther.getKuvent());
            cStm.setDouble(10, indicatorOther.getKugvs());
            cStm.setDouble(11, indicatorOther.getKpco());
            cStm.setDouble(12, indicatorOther.getKdgco());
            cStm.setDouble(13, indicatorOther.getK1min());
            cStm.setDouble(14, indicatorOther.getK1max());
            cStm.setDouble(15, indicatorOther.getK2min());
            cStm.setDouble(16, indicatorOther.getK2max());
            cStm.setDouble(17, indicatorOther.getKpgvs());
            cStm.setDouble(18, indicatorOther.getKdggvs());
            cStm.setDouble(19, indicatorOther.getKqgvsl());
            cStm.setDouble(20, indicatorOther.getGvsdt());
            cStm.setDouble(21, indicatorOther.getKgvsc());
            cStm.setString(22, login);
            cStm.setString(23, ip);

            cStm.executeUpdate();

            LOG.info("update norm indicator other " + indicatorOther + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка обновления граничных значений");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
