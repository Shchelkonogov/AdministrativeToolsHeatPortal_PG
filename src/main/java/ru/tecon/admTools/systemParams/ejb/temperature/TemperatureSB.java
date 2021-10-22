package ru.tecon.admTools.systemParams.ejb.temperature;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureProp;

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
 * Stateless bean для получения данных по формам температурные графики и суточные снижения
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class TemperatureSB {

    private static final Logger LOGGER = Logger.getLogger(TemperatureSB.class.getName());

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    public List<Temperature> getTemperatures(String select) {
        List<Temperature> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new Temperature(res.getInt("graph_id"), res.getString("code"), res.getString("name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load temperatures", e);
        }
        return result;
    }

    public List<TemperatureProp> loadTemperatureProps(Temperature temperature, String select) {
        List<TemperatureProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            stm.setInt(1, temperature.getId());

            ResultSet res = stm.executeQuery();

            while (res.next()) {
                result.add(new TemperatureProp(res.getInt("x"), res.getInt("y")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load temperature props", e);
        }

        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeTemperature(Temperature temperature, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, temperature.getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            LOGGER.info("remove temperature " + temperature.getName() + " result " + cStm.getInt(1) + " message " + cStm.getString(3));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, id);
            cStm.setInt(3, temperatureProp.getTnv());
            cStm.registerOutParameter(4, Types.VARCHAR);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("remove temperature property " + temperatureProp + " for temperature id " + id +
                    " result " + cStm.getInt(1) + " message " + cStm.getString(4));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(4));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addTemperatureProp(int id, TemperatureProp temperatureProp, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, id);
            cStm.setInt(3, temperatureProp.getTnv());
            cStm.setInt(4, temperatureProp.getValue());
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("add temperature property " + temperatureProp + " for temperature id " + id +
                    " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Новое значение " + temperatureProp.getTnv() + " " + temperatureProp.getValue() +
                        ": невозможно создать");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int createTemperature(Temperature temperature, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, temperature.getName());
            cStm.setString(3, temperature.getDescription());
            cStm.registerOutParameter(4, Types.INTEGER);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("create temperature " + temperature + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка добавления справочника " + temperature.getName() + " " + temperature.getDescription());
            }

            return cStm.getInt(4);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
