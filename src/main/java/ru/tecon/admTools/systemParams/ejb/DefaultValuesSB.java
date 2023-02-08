package ru.tecon.admTools.systemParams.ejb;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для загрузки данных по форме значения по умолчанию
 * @author Maksim Shchelkonogov
 */
@Stateless
@LocalBean
public class DefaultValuesSB {

    private static final Logger LOGGER = Logger.getLogger(DefaultValuesSB.class.getName());

    private static final String SEL_OBJECT_TYPES = "select * from sys_0001t.sel_obj_type()";
    private static final String SEL_DEFAULT_OBJECT_TYPE_ID = "select * from sys_0001t.sel_obj_type_def()";
    private static final String FUN_UPD_DEFAULT_OBJECT_TYPE = "call sys_0001t.upd_obj_type_def(?, ?, ?, ?)";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    /**
     * Получение списка типов объектов
     * @return список типов объектов
     */
    public List<ObjectType> getObjectTypes() {
        List<ObjectType> result = new LinkedList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_OBJECT_TYPES)) {
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new ObjectType(res.getInt("obj_type_id"), res.getString("obj_type_name"), res.getString("obj_type_char")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load object types", e);
        }
        return result;
    }

    /**
     * выгрузка идентификатора типа объекта по умолчанию
     * @return идентификатор объекта
     * @throws SystemParamException в случае ошибки выгрузки из базы
     */
    public int getDefaultObjectTypeID() throws SystemParamException {
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SEL_DEFAULT_OBJECT_TYPE_ID)) {
            ResultSet res = stm.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error load default object type", e);
        }

        throw new SystemParamException("error load default object type");
    }

    /**
     * обновление типа объекта по умолчанию
     * @param objectType тип объекта
     * @param login пользователь
     * @param ip адрес
     * @throws SystemParamException в случае ошибки загрузки данных
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDefaultObjectType(ObjectType objectType, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(FUN_UPD_DEFAULT_OBJECT_TYPE)) {
            cStm.setInt(1, objectType.getId());
            cStm.setString(2, login);
            cStm.setString(3, ip);
            cStm.registerOutParameter(4, Types.SMALLINT);

            cStm.executeUpdate();

            LOGGER.info("update default type " + objectType);

            if (cStm.getShort(4) != 0) {
                throw new SystemParamException("Ошибка изменения типа объекта по умолчанию");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "error update default type", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }
}
