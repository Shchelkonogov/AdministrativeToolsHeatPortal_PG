package ru.tecon.admTools.systemParams.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import ru.tecon.admTools.linker.ejb.LinkerStateless;
import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.DataSourceObject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 24.09.2025
 */
@Stateless
@LocalBean
public class DataSourceSB {

    private static final String SELECT_SOURCE = "select * from sys_0001t.sel_opc_clients()";
    private static final String UPDATE_SOURCE = "call sys_0001t.upd_opc_client(?, ?, ?, ?, ?)";
    private static final String ADD_SOURCE = "call sys_0001t.add_opc_client(?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_SOURCE = "call sys_0001t.del_opc_client(?, ?, ?, ?)";

    @Inject
    private Logger logger;

    @EJB
    private LinkerStateless linker;

    @EJB
    private DataSourceSB self;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка ресурсов
     *
     * @return список ресурсов
     */
    public List<DataSourceObject> getSourceTable(String sessionId, String login, String ip) throws SystemParamException {
        List<DataSourceObject> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_SOURCE)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                DataSourceObject source = new DataSourceObject(res.getString("client_name"), res.getString("ip_address"), res.getString("licence"));

                checkSource(source, sessionId, login, ip);

                result.add(source);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load source table", e);
        }

        return result;
    }

    private void checkSource(DataSourceObject source, String sessionId, String login, String ip) throws SystemParamException {
        String status;
        if (linker.checkLicense(source.getName(), sessionId)) {
            status = "Y";
        } else {
            status = "N";
        }

        if (!source.getStatus().equals(status)) {
            source.setStatus(status);
            self.updateSource(source, login, ip);
        }
    }

    /**
     * Обновление информации по ресурсу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSource(DataSourceObject source, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(UPDATE_SOURCE)) {
            cStm.setString(1, source.getName());
            cStm.setString(2, source.getStatus());
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.SMALLINT);

            cStm.executeUpdate();
            if (cStm.getShort(5) != 0) {
                throw new SystemParamException("Ошибка записи источника данных " + source);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Добавление нового ресурса
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addSource(DataSourceObject source, String sessionId, String login, String ip) throws SystemParamException {
        if (linker.checkLicense(source.getName(), sessionId)) {
            source.setStatus("Y");
        } else {
            source.setStatus("N");
        }

        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_SOURCE)) {
            cStm.setString(1, source.getUrl());
            cStm.setString(2, source.getName());
            cStm.setString(3, source.getStatus());
            cStm.setString(4, login);
            cStm.setString(5, ip);
            cStm.registerOutParameter(6, Types.SMALLINT);

            cStm.executeUpdate();
            if (cStm.getShort(6) != 0) {
                throw new SystemParamException("Ошибка записи источника данных " + source);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление ресурса
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeSource(DataSourceObject source, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(REMOVE_SOURCE)) {
            cStm.setString(1, source.getUrl());
            cStm.setString(2, login);
            cStm.setString(3, ip);
            cStm.registerOutParameter(4, Types.SMALLINT);

            cStm.executeUpdate();
            if (cStm.getShort(4) != 0) {
                throw new SystemParamException("Ошибка удаления источника данных " + source);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
