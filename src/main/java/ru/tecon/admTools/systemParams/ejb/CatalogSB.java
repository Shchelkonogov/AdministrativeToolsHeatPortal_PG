package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.catalog.CatalogProp;
import ru.tecon.admTools.systemParams.model.catalog.CatalogType;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для загрузки данных по форме справочники
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class CatalogSB {

    private static final Logger LOGGER = Logger.getLogger(CatalogSB.class.getName());

    private static final String SEL_CATALOG_TYPES = "select * from table(sys_0001t.sel_sp_header())";
    private static final String SEL_CATALOG_PROPS = "select * from table(sys_0001t.sel_sp_body(?))";
    private static final String FUN_REMOVE_CATALOG_TYPE = "{? = call sys_0001t.del_sp_header(?, ?, ?, ?)}";
    private static final String FUN_REMOVE_CATALOG_PROP = "{? = call sys_0001t.del_sp_body(?, ?, ?, ?, ?)}";
    private static final String FUN_CREATE_NEW_CATALOG_PROP = "{? = call sys_0001t.add_sp_body(?, ?, ?, ?, ?)}";
    private static final String FUN_CREATE_NEW_CATALOG_TYPE = "{? = call sys_0001t.add_sp_header(?, ?, ?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка справочников
     * @return список справочников
     */
    public List<CatalogType> getCatalogTypes() {
        List<CatalogType> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_CATALOG_TYPES)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new CatalogType(res.getInt("sp_header_id"), res.getString("sp_header_name")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load catalog types", e);
        }
        return result;
    }

    /**
     * Загузка значений справочника
     * @param catalogType справочник
     */
    public void loadCatalogProps(CatalogType catalogType) {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_CATALOG_PROPS)) {
            stm.setFetchSize(1000);
            stm.setInt(1, catalogType.getId());

            ResultSet res = stm.executeQuery();
            List<CatalogProp> result = new ArrayList<>();
            while (res.next()) {
                result.add(new CatalogProp(res.getLong("sp_body_id"), res.getString("sp_body_name")));
            }

            catalogType.setCatalogProps(result);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load catalog props", e);
        }
    }

    /**
     * Удаление справочника
     * @param catalogType справочник
     * @param login пользователь
     * @param ip адресс
     * @throws SystemParamException ошибка удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeCatalog(CatalogType catalogType, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_REMOVE_CATALOG_TYPE)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, catalogType.getId());
            cStm.registerOutParameter(3, Types.VARCHAR);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            LOGGER.info("remove catalog " + catalogType.getTypeName() + " result " + cStm.getInt(1) + " message " + cStm.getString(3));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(3));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаление значения справочника
     * @param catalogTypeID идентификатор справочника
     * @param catalogProp значение справочника для удаления
     * @param login пользователь
     * @param ip адресс
     * @throws SystemParamException ошибка удаления
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeCatalogProp(Integer catalogTypeID, CatalogProp catalogProp, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_REMOVE_CATALOG_PROP)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, catalogTypeID);
            cStm.setLong(3, catalogProp.getId());
            cStm.registerOutParameter(4, Types.VARCHAR);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("remove catalog property " + catalogProp.getPropName() + " for struct id " + catalogTypeID +
                    " result " + cStm.getInt(1) + " message " + cStm.getString(4));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(cStm.getString(4));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Создание нового значения справочника
     * @param catalogID идентификатор справочника
     * @param newPropName имя нового значения справочника
     * @param login пользователь
     * @param ip адрес
     * @throws SystemParamException ошибка создания
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addCatalogProp(Integer catalogID, String newPropName, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_CREATE_NEW_CATALOG_PROP)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setInt(2, catalogID);
            cStm.setString(3, newPropName);
            cStm.registerOutParameter(4, Types.INTEGER);
            cStm.setString(5, login);
            cStm.setString(6, ip);

            cStm.executeUpdate();

            LOGGER.info("add catalog property " + newPropName + " for struct id " + catalogID +
                    " result " + cStm.getInt(1) + " new prop ID " + cStm.getInt(4));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException(newPropName + ": невозможно создать");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Создание нового справочника
     * @param catalogTypeName имя нового справочника
     * @param login пользователь
     * @param ip адресс
     * @return идентификатор нового справочника
     * @throws SystemParamException ошибка создания
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int createCatalogType(String catalogTypeName, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_CREATE_NEW_CATALOG_TYPE)) {
            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, catalogTypeName);
            cStm.registerOutParameter(3, Types.INTEGER);
            cStm.setString(4, login);
            cStm.setString(5, ip);

            cStm.executeUpdate();

            LOGGER.info("create catalog type " + catalogTypeName + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка добавления справочника " + catalogTypeName);
            }

            return cStm.getInt(3);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
