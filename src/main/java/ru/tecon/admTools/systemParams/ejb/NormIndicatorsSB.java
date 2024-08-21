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

    private static final String SEL_METROLOGY = "select * from sys_0001t.sel_ni_metro()";
    private static final String FUN_UPD_METROLOGY = "call sys_0001t.upd_ni_metro(?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_BORDER_VALUES_CO = "select * from sys_0001t.sel_ni_rs_co()";
    private static final String FUN_BORDER_VALUES_CO = "call sys_0001t.upd_ni_rs_co(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_BORDER_VALUES_VENT = "select * from sys_0001t.sel_ni_rs_vent()";
    private static final String FUN_BORDER_VALUES_VENT = "call sys_0001t.upd_ni_rs_vent(?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_BORDER_VALUES_GVS = "select * from sys_0001t.sel_ni_rs_gvs()";
    private static final String FUN_BORDER_VALUES_GVS = "call sys_0001t.upd_ni_rs_gvs(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_TV = "select * from sys_0001t.sel_norm_ind_tv()";
    private static final String FUN_UPD_TV = "call sys_0001t.upd_norm_ind_tv(?, ?, ?, ?, ?, ?)";

    private static final String SEL_CO = "select * from sys_0001t.sel_norm_ind_co(?)";
    private static final String FUN_UPD_CO = "call sys_0001t.upd_norm_ind_co(?, ?, ?, ?, ?, ?)";

    private static final String SEL_GVS = "select * from sys_0001t.sel_norm_ind_gvs(?)";
    private static final String FUN_UPD_GVS = "call sys_0001t.upd_norm_ind_gvs(?, ?, ?, ?, ?)";

    private static final String SEL_VENT = "select * from sys_0001t.sel_norm_ind_vent(?)";
    private static final String FUN_UPD_VENT = "call sys_0001t.upd_norm_ind_vent(?, ?, ?, ?, ?, ?)";

    private static final String SEL_T7 = "select * from sys_0001t.sel_norm_ind_t7_uu()";
    private static final String FUN_UPD_T7_ITP = "call sys_0001t.upd_norm_ind_t7_uu_itp(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_UPD_T7_CTP = "call sys_0001t.upd_norm_ind_t7_uu_ctp(?, ?, ?, ?, ?, ?, ?)";

    private static final String SEL_DT7 = "select * from sys_0001t.sel_norm_ind_dt_uu()";
    private static final String FUN_UPD_DT7 = "call sys_0001t.upd_norm_ind_dt_uu(?, ?, ?, ?, ?, ?)";

    private static final String SEL_UNDERSUPPLY = "select * from sys_0001t.sel_norm_ind_no()";
    private static final String FUN_UPD_UNDERSUPPLY = "call sys_0001t.upd_norm_ind_no(?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            cStm.setObject(1, indicatorTV.getKpn(), Types.NUMERIC);
            cStm.setObject(2, indicatorTV.getKz(), Types.NUMERIC);
            cStm.setObject(3, indicatorTV.getKp(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update norm indicator TV " + indicatorTV + " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
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
            cStm.setInt(1, indicatorCO.getId());
            cStm.setObject(2, indicatorCO.getKz(), Types.NUMERIC);
            cStm.setObject(3, indicatorCO.getKp(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update norm indicator CO " + indicatorCO + " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
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
            cStm.setInt(1, indicatorGVS.getId());
            cStm.setObject(2, indicatorGVS.getKc(), Types.NUMERIC);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update norm indicator GVS " + indicatorGVS + " result " + cStm.getShort(5));

            if (cStm.getShort(5) != 0) {
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
            cStm.setInt(1, indicatorVENT.getId());
            cStm.setObject(2, indicatorVENT.getKz(), Types.NUMERIC);
            cStm.setObject(3, indicatorVENT.getKp(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update norm indicator VENT " + indicatorVENT + " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка обновления показателя вентиляции");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка показателей T7
     * @return список данных
     */
    public List<IndicatorT7> getT7() {
        List<IndicatorT7> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_T7)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                String name;
                switch (res.getString("tp_type")) {
                    case "I":
                        name = "ИТП";
                        break;
                    case "C":
                        name = "ЦТП";
                        break;
                    default:
                        name = "";
                }

                result.add(new IndicatorT7(res.getString("tp_type"), name, res.getDouble("t7_min_g"),
                        res.getDouble("t7_max_g"), res.getDouble("t7_min_p"), res.getDouble("t7_max_p")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load T7 indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей T7
     * @param indicatorT7 новые показатели
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateT7(IndicatorT7 indicatorT7, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection()) {
            String select;
            switch (indicatorT7.getId()) {
                case "I":
                    select = FUN_UPD_T7_ITP;
                    break;
                case "C":
                    select = FUN_UPD_T7_CTP;
                    break;
                default:
                    return;
            }
            try (CallableStatement cStm = connect.prepareCall(select)) {
                cStm.setObject(1, indicatorT7.getMinG(), Types.NUMERIC);
                cStm.setObject(2, indicatorT7.getMinP(), Types.NUMERIC);
                cStm.setObject(3, indicatorT7.getMaxG(), Types.NUMERIC);
                cStm.setObject(4, indicatorT7.getMaxP(), Types.NUMERIC);
                cStm.setString(5, login);
                cStm.setString(6, ip);
                cStm.registerOutParameter(7, Types.SMALLINT);

                cStm.executeUpdate();

                LOG.info("update T7 indicator " + indicatorT7 + " result " + cStm.getShort(7));

                if (cStm.getShort(7) != 0) {
                    throw new SystemParamException("Ошибка обновления показателя T7");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка показателей ΔT7
     * @return список данных
     */
    public List<IndicatorDT7> getDT7() {
        List<IndicatorDT7> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_DT7)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorDT7(res.getDouble("dt_min_g"), res.getDouble("dt_max_g"), res.getDouble("dt_norm_p")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load DT7 indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей ΔT7
     * @param indicatorDT7 новые показатели
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDT7(IndicatorDT7 indicatorDT7, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_DT7)) {
            cStm.setObject(1, indicatorDT7.getDtMin(), Types.NUMERIC);
            cStm.setObject(2, indicatorDT7.getDtMax(), Types.NUMERIC);
            cStm.setObject(3, indicatorDT7.getDtNorm(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update ΔT7 indicator " + indicatorDT7 + " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка обновления показателя ΔT7");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Получение списка показателей недоотпуска
     * @return список данных
     */
    public List<IndicatorUnderSupply> getUnderSupply() {
        List<IndicatorUnderSupply> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_UNDERSUPPLY)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorUnderSupply(res.getDouble("t3_k1"), res.getDouble("t3_k2"), res.getDouble("t3_k3"),
                        res.getDouble("t4_k1"), res.getDouble("t4_k2"), res.getDouble("t4_k3")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load underSupply indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей недоотпуска
     * @param indicatorUnderSupply новые значения
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnderSupply(IndicatorUnderSupply indicatorUnderSupply, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_UNDERSUPPLY)) {
            cStm.setObject(1, indicatorUnderSupply.getT3K1(), Types.NUMERIC);
            cStm.setObject(2, indicatorUnderSupply.getT3K2(), Types.NUMERIC);
            cStm.setObject(3, indicatorUnderSupply.getT3K3(), Types.NUMERIC);
            cStm.setObject(4, indicatorUnderSupply.getT4K1(), Types.NUMERIC);
            cStm.setObject(5, indicatorUnderSupply.getT4K2(), Types.NUMERIC);
            cStm.setObject(6, indicatorUnderSupply.getT4K3(), Types.NUMERIC);
            cStm.setString(7, login);
            cStm.setString(8, ip);
            cStm.registerOutParameter(9, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update underSupply indicator " + indicatorUnderSupply + " result " + cStm.getShort(9));

            if (cStm.getShort(9) != 0) {
                throw new SystemParamException("Ошибка обновления показателя недоотпуск");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера при обновлении показателя недоотпуск");
        }
    }

    /**
     * Получение списка показателей метрологических погрешностей
     *
     * @return список данных
     */
    public List<IndicatorMetrology> getMetrology() {
        List<IndicatorMetrology> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_METROLOGY)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorMetrology(res.getDouble("kt"),
                        res.getDouble("kp"),
                        res.getDouble("kg"),
                        res.getDouble("kq")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load VENT indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей метрологических погрешностей
     *
     * @param indicatorMetrology новые значения
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateMetrology(IndicatorMetrology indicatorMetrology, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_METROLOGY)) {
            cStm.setObject(1, indicatorMetrology.getKt(), Types.NUMERIC);
            cStm.setObject(2, indicatorMetrology.getKp(), Types.NUMERIC);
            cStm.setObject(3, indicatorMetrology.getKgKv(), Types.NUMERIC);
            cStm.setObject(4, indicatorMetrology.getKq(), Types.NUMERIC);
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update metrology indicator " + indicatorMetrology + " result " + cStm.getShort(7));

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка обновления показателя метрологических погрешностей");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера при обновлении показателя метрологических погрешностей");
        }
    }

    /**
     * Получение списка показателей граничные значения цо
     *
     * @return список данных
     */
    public List<IndicatorBorderCo> getBorderValuesCo() {
        List<IndicatorBorderCo> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_BORDER_VALUES_CO)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorBorderCo(res.getDouble("co_kdt"),
                        res.getDouble("co_kdto"),
                        res.getDouble("co_ku"),
                        res.getDouble("kdgco"),
                        res.getDouble("kpco"),
                        res.getDouble("k1"),
                        res.getDouble("k2")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load border co indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей граничные значения - центральное отопление
     *
     * @param indicatorBorderCo новые значения
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBorderValuesCo(IndicatorBorderCo indicatorBorderCo, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_BORDER_VALUES_CO)) {
            cStm.setObject(1, indicatorBorderCo.getKdt(), Types.NUMERIC);
            cStm.setObject(2, indicatorBorderCo.getKdto(), Types.NUMERIC);
            cStm.setObject(3, indicatorBorderCo.getKy(), Types.NUMERIC);
            cStm.setObject(4, indicatorBorderCo.getKdq(), Types.NUMERIC);
            cStm.setObject(5, indicatorBorderCo.getK(), Types.NUMERIC);
            cStm.setObject(6, indicatorBorderCo.getK1(), Types.NUMERIC);
            cStm.setObject(7, indicatorBorderCo.getK2(), Types.NUMERIC);
            cStm.setString(8, login);
            cStm.setString(9, ip);
            cStm.registerOutParameter(10, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update border values co " + indicatorBorderCo + " result " + cStm.getShort(10));

            if (cStm.getShort(10) != 0) {
                throw new SystemParamException("Ошибка обновления показателя граничные значения - центральное отопление");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера при обновлении показателя граничные значения - центральное отопление");
        }
    }

    /**
     * Получение списка показателей граничные значения вентиляции
     *
     * @return список данных
     */
    public List<IndicatorBorderVent> getBorderValuesVent() {
        List<IndicatorBorderVent> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_BORDER_VALUES_VENT)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorBorderVent(res.getDouble("vent_kdt"),
                        res.getDouble("vent_kdto"),
                        res.getDouble("vent_ku"),
                        res.getDouble("kdgvent")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load border vent indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей граничные значения - вентиляция
     *
     * @param indicatorBorderVent новые значения
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBorderValuesVent(IndicatorBorderVent indicatorBorderVent, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_BORDER_VALUES_VENT)) {
            cStm.setObject(1, indicatorBorderVent.getKdt(), Types.NUMERIC);
            cStm.setObject(2, indicatorBorderVent.getKdto(), Types.NUMERIC);
            cStm.setObject(3, indicatorBorderVent.getKy(), Types.NUMERIC);
            cStm.setObject(4, indicatorBorderVent.getKdg(), Types.NUMERIC);
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update border values vent " + indicatorBorderVent + " result " + cStm.getShort(7));

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка обновления показателя граничные значения - вентиляция");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера при обновлении показателя граничные значения - вентиляция");
        }
    }

    /**
     * Получение списка показателей граничные значения гвс
     *
     * @return список данных
     */
    public List<IndicatorBorderGvs> getBorderValuesGvs() {
        List<IndicatorBorderGvs> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_BORDER_VALUES_GVS)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                result.add(new IndicatorBorderGvs(res.getDouble("gvs_kdt"),
                        res.getDouble("gvs_kdto"),
                        res.getDouble("gvs_ku"),
                        res.getDouble("gvs_dt"),
                        res.getDouble("gvs_dt7"),
                        res.getDouble("gvs_t7"),
                        res.getDouble("kpgvs")));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "error load border gvs indicator", e);
        }
        return result;
    }

    /**
     * Обновление списка показателей граничные значения - гвс
     *
     * @param indicatorBorderGvs новые значения
     * @param login идентификатор пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateBorderValuesGvs(IndicatorBorderGvs indicatorBorderGvs, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_BORDER_VALUES_GVS)) {
            cStm.setObject(1, indicatorBorderGvs.getKdt(), Types.NUMERIC);
            cStm.setObject(2, indicatorBorderGvs.getKdto(), Types.NUMERIC);
            cStm.setObject(3, indicatorBorderGvs.getKy(), Types.NUMERIC);
            cStm.setObject(4, indicatorBorderGvs.getDt(), Types.NUMERIC);
            cStm.setObject(5, indicatorBorderGvs.getDt7(), Types.NUMERIC);
            cStm.setObject(6, indicatorBorderGvs.getT7(), Types.NUMERIC);
            cStm.setObject(7, indicatorBorderGvs.getKgvs(), Types.NUMERIC);
            cStm.setString(8, login);
            cStm.setString(9, ip);
            cStm.registerOutParameter(10, Types.SMALLINT);

            cStm.executeUpdate();

            LOG.info("update border values gvs " + indicatorBorderGvs + " result " + cStm.getShort(10));

            if (cStm.getShort(10) != 0) {
                throw new SystemParamException("Ошибка обновления показателя граничные значения - гвс");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера при обновлении показателя граничные значения - гвс");
        }
    }
}
