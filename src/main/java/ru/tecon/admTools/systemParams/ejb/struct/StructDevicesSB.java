package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.struct.StructType;
import ru.tecon.admTools.systemParams.model.struct.StructTypeProp;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 17.01.2023
 */
@Stateless(name = "structDevicesSB", mappedName = "ejb/structDevicesSB")
@Remote(StructCurrentRemote.class)
public class StructDevicesSB implements StructCurrentRemote {

    private static final Logger LOGGER = Logger.getLogger(StructDevicesSB.class.getName());

    private static final String SELECT_STRUCT_TYPES = "select dev_type_id as type_id, dev_type_name as type_name, dev_type_char as type_char, parent_id " +
            "from table(sys_0001t.sel_dev_type) " +
                "connect by prior dev_type_id = parent_id start with parent_id is null " +
                "order siblings by dev_type_name";
    private static final String SELECT_STRUCT_TYPE_PROPS = "select * from ( " +
            "select a.dev_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
                "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, c.prop_cat_name, " +
                "d.measure_name, d.short_name, a.display_id " +
                    "from table(sys_0001t.sel_dev_type_props(?)) a, " +
                            "table(sys_0001t.sel_type_props_type()) b, " +
                            "table(sys_0001t.sel_type_props_cat()) c, " +
                            "table(sys_0001t.sel_measure()) d " +
                        "where b.prop_val_type = a.prop_type and c.prop_cat_id = a.prop_cat and d.measure_id = a.prop_measure " +
                "union all " +
                "select a.dev_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
                "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, null, " +
                "d.measure_name, d.short_name, a.display_id " +
                    "from table(sys_0001t.sel_dev_type_props(?)) a, " +
                            "table(sys_0001t.sel_type_props_type()) b, " +
                            "table(sys_0001t.sel_measure()) d " +
                        "where a.prop_cat = 'S' and b.prop_val_type = a.prop_type and d.measure_id = a.prop_measure) " +
            "order by display_id";

    private static final String ADD_STRUCT_TYPE = "{? = call sys_0001t.add_dev_type(?, ?, ?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE = "{? = call sys_0001t.del_dev_type(?, ?, ?, ?)}";
    private static final String ADD_STRUCT_TYPE_PROP = "{? = call sys_0001t.add_dev_type_prop(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE_PROP = "{? = call sys_0001t.del_dev_type_prop(?, ?, ?, ?, ?)}";

    private static final String MOVE_PROP_UP = "{? = call sys_0001t.set_dev_type_prop_up(?, ?)}";
    private static final String MOVE_PROP_DOWN = "{? = call sys_0001t.set_dev_type_prop_down(?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @EJB
    private StructSB wrapperDivisions;

    @Override
    public void removeStruct(StructType structType, String login, String ip) throws SystemParamException {
        wrapperDivisions.removeStruct(structType, login, ip, REMOVE_STRUCT_TYPE);
    }

    @Override
    public void removeStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException {
        wrapperDivisions.removeStructProp(structTypeID, structTypeProp, login, ip, REMOVE_STRUCT_TYPE_PROP);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int addStruct(StructType structType, String login, String ip) throws SystemParamException {
        try (Connection connect = ds.getConnection();
             CallableStatement cStm = connect.prepareCall(ADD_STRUCT_TYPE)) {

            cStm.registerOutParameter(1, Types.INTEGER);
            cStm.setString(2, structType.getName());
            cStm.setString(3, structType.getTypeChar());
            if (structType.getParentID() == null) {
                cStm.setNull(4, Types.INTEGER);
            } else {
                cStm.setInt(4, structType.getParentID());
            }
            cStm.setString(5, structType.getName());
            cStm.registerOutParameter(6, Types.INTEGER);
            cStm.setString(7, login);
            cStm.setString(8, ip);

            cStm.executeUpdate();

            LOGGER.info("add struct " + structType.getName() + " result " + cStm.getInt(1));

            if (cStm.getInt(1) != 0) {
                throw new SystemParamException("Ошибка добавления структуры " + structType.getName());
            }

            return cStm.getInt(6);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
            throw new SystemParamException("Внутренняя ошибка сервера");
        }
    }

    @Override
    public void addStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException {
        wrapperDivisions.addStructProp(structTypeID, structTypeProp, login, ip, ADD_STRUCT_TYPE_PROP);
    }

    @Override
    public List<StructType> getStructTypes() {
        List<StructType> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STRUCT_TYPES)) {
            stm.setFetchSize(500);

            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StructType(res.getInt("type_id"),
                        res.getString("type_name"),
                        res.getString("type_char"),
                        res.getObject("parent_id", Integer.class)));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    @Override
    public List<StructTypeProp> getStructTypeProps(int id) {
        return wrapperDivisions.getStructTypeProps(id, SELECT_STRUCT_TYPE_PROPS);
    }

    @Override
    public void moveProp(int typeID, int propID, int oldPosition, int newPosition) throws SystemParamException {
        wrapperDivisions.moveProp(typeID, propID, oldPosition, newPosition, MOVE_PROP_UP, MOVE_PROP_DOWN);
    }
}
