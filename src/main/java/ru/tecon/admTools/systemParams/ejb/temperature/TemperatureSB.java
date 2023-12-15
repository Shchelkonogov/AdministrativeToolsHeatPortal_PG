package ru.tecon.admTools.systemParams.ejb.temperature;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureProp;
import ru.tecon.admTools.utils.AdmTools;

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
            int columnCount = res.getMetaData().getColumnCount();
            while (res.next()) {
                switch (columnCount) {
                    case 3:
                        result.add(new Temperature(res.getInt("graph_id"), res.getString("code"), res.getString("name")));
                        break;
                    case 5:
                        result.add(new Temperature(res.getInt("graph_id"), res.getString("code"), res.getString("name"),
                                res.getInt("min_val"), res.getInt("max_val")));
                        break;
                }
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
                result.add(new TemperatureProp(res.getInt("x"), res.getDouble("y")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load temperature props", e);
        }

        return result;
    }

    public Temperature getTemperatureById(int id, String select) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(select)) {
            stm.setInt(1, id);

            ResultSet res = stm.executeQuery();

            if (res.next()) {
                return new Temperature(res.getInt("graph_id"), res.getString("code"), res.getString("name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load temperature props", e);
        }

        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeTemperature(Temperature temperature, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            cStm.setInt(1, temperature.getId());
            cStm.registerOutParameter(2, Types.VARCHAR);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("remove temperature " + temperature.getName() + " result " + cStm.getShort(5) + " message " + cStm.getString(2));

            if (cStm.getShort(5) != 0) {
                throw new SystemParamException(cStm.getString(2));
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
            cStm.setInt(1, id);
            cStm.setInt(2, temperatureProp.getTnv());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("remove temperature property " + temperatureProp + " for temperature id " + id +
                    " result " + cStm.getShort(6) + " message " + cStm.getString(3));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException(cStm.getString(3));
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
            cStm.setInt(1, id);
            cStm.setObject(2, temperatureProp.getTnv(), Types.NUMERIC);
            cStm.setObject(3, temperatureProp.getValue(), Types.NUMERIC);
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("add temperature property " + temperatureProp + " for temperature id " + id +
                    " result " + cStm.getShort(6));

            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Новое значение " + temperatureProp.getTnv() + " " + temperatureProp.getValue() +
                        ": невозможно создать");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException(AdmTools.getSQLExceptionMessage(e));
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int createTemperature(Temperature temperature, String login, String ip, String select) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(select)) {
            int index;

            cStm.setString(1, temperature.getName());
            cStm.setString(2, temperature.getDescription());
            if ((temperature.getMin() != null) && (temperature.getMax() != null)) {
                cStm.setInt(3, temperature.getMin());
                cStm.setInt(4, temperature.getMax());
                cStm.registerOutParameter(5, Types.INTEGER);
                cStm.setString(6, login);
                cStm.setString(7, ip);

                index = 5;
            } else {
                cStm.registerOutParameter(3, Types.INTEGER);
                cStm.setString(4, login);
                cStm.setString(5, ip);

                index = 3;
            }

            cStm.registerOutParameter(index + 3, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("create temperature " + temperature + " result " + cStm.getShort(index + 3));

            if (cStm.getShort(index + 3) != 0) {
                throw new SystemParamException("Ошибка добавления справочника " + temperature.getName() + " " + temperature.getDescription());
            }

            return cStm.getInt(index);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
