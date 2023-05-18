package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.ObjectLink;

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
 * Stateless bean для получения данных по форме связь объектов
 *
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class ObjectLinksSB {

    private static final String SELECT_OBJECT_LINKS = "select * from sys_0001t.sel_rel_type()";
    private static final String FUN_UPDATE_REL_TYPE = "call sys_0001t.upd_rel_type(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_ADD_REL_TYPE = "call sys_0001t.add_rel_type(?, ?, ?, ?, ?, ?, ?)";
    private static final String FUN_DEL_REL_TYPE = "call sys_0001t.del_rel_type(?, ?, ?, ?, ?)";

    @Inject
    private Logger logger;

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка связей объектов
     *
     * @return связи объектов
     */
    public List<ObjectLink> getObjectLinks() {
        List<ObjectLink> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_OBJECT_LINKS)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ObjectLink(res.getInt("rel_type_id"), res.getString("rel_type_name"),
                        res.getInt("obj_type_id1"), res.getInt("obj_type_id2")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error load object types", e);
        }
        return result;
    }

    /**
     * Обновление существующей связи
     *
     * @param link  связь
     * @param login имя пользователя
     * @param ip    аддрес пользователя
     * @throws SystemParamException в случае ошибки записи в базу
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateObjectLink(ObjectLink link, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPDATE_REL_TYPE)) {
            cStm.setInt(1, link.getId());
            cStm.setString(2, link.getName());
            cStm.setInt(3, link.getObjectTypeLink1());
            cStm.setInt(4, link.getObjectTypeLink2());
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            logger.info("update link " + link.getName() + " result " + cStm.getShort(7));

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка обновления " + link.getName());
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error update object link", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Создание новой связи
     *
     * @param link  связь
     * @param login имя пользователя
     * @param ip    аддрес пользователя
     * @return уникальный идентификатор, присвоенный базой
     * @throws SystemParamException в случае ошибки создания новой связи в базе
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int addObjectLink(ObjectLink link, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_ADD_REL_TYPE)) {
            cStm.setString(1, link.getName());
            cStm.setInt(2, link.getObjectTypeLink1());
            cStm.setInt(3, link.getObjectTypeLink2());
            cStm.registerOutParameter(4, Types.INTEGER);
            cStm.setString(5, login);
            cStm.setString(6, ip);
            cStm.registerOutParameter(7, Types.SMALLINT);

            cStm.executeUpdate();

            logger.info("add link " + link.getName() + " result " + cStm.getShort(7) + " new id " + cStm.getInt(4));

            if (cStm.getShort(7) != 0) {
                throw new SystemParamException("Ошибка создания " + link.getName());
            }
            return cStm.getInt(4);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error add object link", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    /**
     * Удаления связи
     *
     * @param link  связь
     * @param login имя пользователя
     * @param ip    аддрес пользователя
     * @throws SystemParamException ошибка удаления свзяи в базе
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeObjectLink(ObjectLink link, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_DEL_REL_TYPE)) {
            cStm.setInt(1, link.getId());
            cStm.registerOutParameter(2, Types.VARCHAR);
            cStm.setString(3, login);
            cStm.setString(4, ip);
            cStm.registerOutParameter(5, Types.INTEGER);

            cStm.executeUpdate();

            logger.info("remove link " + link.getName() + " result " + cStm.getInt(5));

            if (cStm.getInt(5) != 0) {
                throw new SystemParamException("Ошибка удаления " + cStm.getString(2));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "error remove object link", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
