package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.Measure;
import ru.tecon.admTools.systemParams.model.struct.*;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean для работы с формой агрегаты из подкласса структур
 * @author Maksim Shchelkonogov
 */
@Stateless(name = "structProcessesSB", mappedName = "ejb/structProcessesSB")
@Remote(StructCurrentRemote.class)
public class StructProcessesSB implements StructCurrentRemote {

    private static final String SELECT_STRUCT_TYPES = "select techproc_type_id as type_id, techproc_type_name as type_name, techproc_type_char as type_char " +
            "from table(sys_0001t.sel_techproc_type())";
    private static final String SELECT_STRUCT_TYPE_PROPS = "select * from ( " +
            "select a.techproc_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
                "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, c.prop_cat_name, " +
                "d.measure_name, d.short_name, a.display_id " +
                    "from table(sys_0001t.sel_techproc_type_props(?)) a, " +
                            "table(sys_0001t.sel_type_props_type()) b, " +
                            "table(sys_0001t.sel_type_props_cat()) c, " +
                            "table(sys_0001t.sel_measure()) d " +
                        "where b.prop_val_type = a.prop_type and c.prop_cat_id = a.prop_cat and d.measure_id = a.prop_measure " +
                "union all " +
                "select a.techproc_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
                "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, null, " +
                "d.measure_name, d.short_name, a.display_id " +
                    "from table(sys_0001t.sel_techproc_type_props(?)) a, " +
                            "table(sys_0001t.sel_type_props_type()) b, " +
                            "table(sys_0001t.sel_measure()) d " +
                        "where a.prop_cat = 'S' and b.prop_val_type = a.prop_type and d.measure_id = a.prop_measure) " +
            "order by display_id";

    private static final String ADD_STRUCT_TYPE = "{? = call sys_0001t.add_techproc_type(?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE = "{? = call sys_0001t.del_techproc_type(?, ?, ?, ?)}";
    private static final String ADD_STRUCT_TYPE_PROP = "{? = call sys_0001t.add_techproc_type_prop(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE_PROP = "{? = call sys_0001t.del_techproc_type_prop(?, ?, ?, ?, ?)}";

    private static final String MOVE_PROP_UP = "{? = call sys_0001t.set_techproc_type_prop_up(?, ?)}";
    private static final String MOVE_PROP_DOWN = "{? = call sys_0001t.set_techproc_type_prop_down(?, ?)}";

    @Resource(name = "jdbc/DataSource")
    private DataSource ds;

    @EJB
    private StructSB wrapperDivisions;

    @Inject
    private Logger logger;

    @Override
    public void removeStruct(StructType structType, String login, String ip) throws SystemParamException {
        wrapperDivisions.removeStruct(structType, login, ip, REMOVE_STRUCT_TYPE);
    }

    @Override
    public void removeStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException {
        wrapperDivisions.removeStructProp(structTypeID, structTypeProp, login, ip, REMOVE_STRUCT_TYPE_PROP);
    }

    @Override
    public int addStruct(StructType structType, String login, String ip) throws SystemParamException {
        return wrapperDivisions.addStruct(structType, login, ip, ADD_STRUCT_TYPE);
    }

    @Override
    public void addStructProp(int structTypeID, StructTypeProp structTypeProp, String login, String ip) throws SystemParamException {
        wrapperDivisions.addStructProp(structTypeID, structTypeProp, login, ip, ADD_STRUCT_TYPE_PROP);
    }

    @Override
    public List<StructType> getStructTypes() {
        return wrapperDivisions.getStructTypes(SELECT_STRUCT_TYPES);
    }

    @Override
    public List<StructTypeProp> getStructTypeProps(int id) {
        List<StructTypeProp> result = new ArrayList<>();
        try (Connection connect = ds.getConnection();
             PreparedStatement stm = connect.prepareStatement(SELECT_STRUCT_TYPE_PROPS)) {
            stm.setInt(1, id);
            stm.setInt(2, id);
            ResultSet res = stm.executeQuery();
            while (res.next()) {
                result.add(new StructTypeProp(res.getInt("prop_id"), res.getString("prop_name"),
                        new PropValType(res.getString("prop_type"), res.getString("prop_val_type_name")),
                        new PropCat(res.getString("prop_cat"), res.getString("prop_cat_name")),
                        res.getString("prop_def"),
                        new Measure(res.getInt("prop_measure"), res.getString("measure_name"), res.getString("short_name")),
                        new SpHeader(res.getInt("sp_header_id"), res.getString("sp_header_name"))));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        return result;
    }

    @Override
    public void moveProp(int typeID, int propID, int oldPosition, int newPosition) throws SystemParamException {
        wrapperDivisions.moveProp(typeID, propID, oldPosition, newPosition, MOVE_PROP_UP, MOVE_PROP_DOWN);
    }
}
