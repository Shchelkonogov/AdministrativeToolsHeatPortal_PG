package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.SysProp;
import ru.tecon.admTools.systemParams.model.struct.Measure;
import ru.tecon.admTools.systemParams.model.struct.PropValType;

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
 * Stateless bean для работы с формой системные параметры
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class SysPropSB {

    private static final Logger LOGGER = Logger.getLogger(SysPropSB.class.getName());

    private static final String SEL_SYS_PROPS = "select a.sys_prop_id, a.prop_name, a.prop_def, a.prop_type, b.prop_val_type_name, " +
            "a.prop_measure, c.measure_name, c.short_name " +
                "from table(sys_0001t.sel_sys_props()) a, table(sys_0001t.sel_type_props_type) b, table(sys_0001t.sel_measure) c " +
                    "where a.prop_type = b.prop_val_type and a.prop_measure = c.measure_id";
    private static final String FUN_ADD_SYS_PROP = "{? = call sys_0001t.add_new_sys_prop(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String FUN_DEL_SYS_PROP = "{? = call sys_0001t.del_sys_prop(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * @return список системных параметров
     */
    public List<SysProp> getSysProps() {
        List<SysProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_SYS_PROPS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new SysProp(res.getInt("sys_prop_id"), res.getString("prop_name"),
                        new PropValType(res.getString("prop_type"), res.getString("prop_val_type_name")),
                        new Measure(res.getInt("prop_measure"), res.getString("measure_name"), res.getString("short_name")),
                        res.getString("prop_def")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load system props", e);
        }
        return result;
    }

    /**
     * Создание нового системного параметра в базе
     * @param prop системный параметр
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @return id нового системного параметра
     * @throws SystemParamException в случае ошибки создания параметра в базе
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int addSysProp(SysProp prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_SYS_PROP)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, prop.getName());
            cStm.setString(3, prop.getType().getCode());
            cStm.setInt(4, prop.getMeasure().getId());
            cStm.setString(5, prop.getDef());
            cStm.registerOutParameter(6, Types.INTEGER);
            cStm.registerOutParameter(7, Types.VARCHAR);
            cStm.setString(8, login);
            cStm.setString(9, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка добавления " + cStm.getString(7));
            }

            LOGGER.info("add system property " + prop.getName() + " new id " + cStm.getInt(6));

            return cStm.getInt(6);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error add system property", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаления системного параметра из базы
     * @param prop системный параметр
     * @param login имя пользователя
     * @param ip адрес пользователя
     * @throws SystemParamException в случае ошибки удаления параметра из базы
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeSysProp(SysProp prop, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_SYS_PROP)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, prop.getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка удаления " + cStm.getString(3));
            }
            LOGGER.info("remove system property " + prop.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error remove system property", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
