package ru.tecon.admTools.systemParams.ejb.struct;

import ru.tecon.admTools.systemParams.SystemParamException;
import ru.tecon.admTools.systemParams.model.struct.StructType;
import ru.tecon.admTools.systemParams.model.struct.StructTypeProp;

import javax.ejb.*;
import java.util.List;

/**
 * Stateless bean для работы с формой агрегаты из подкласса структур
 * @author Maksim Shchelkonogov
 */
@Stateless(name = "structProcessesSB", mappedName = "ejb/structProcessesSB")
@Remote(StructCurrentRemote.class)
public class StructProcessesSB implements StructCurrentRemote {

    private static final String SELECT_STRUCT_TYPES = "select techproc_type_id as type_id, techproc_type_name as type_name, techproc_type_char as type_char " +
            "from table(sys_0001t.sel_techproc_type())";
    private static final String SELECT_STRUCT_TYPE_PROPS = "select a.techproc_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
            "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, c.prop_cat_name, " +
            "d.measure_name, d.short_name " +
                "from table(sys_0001t.sel_techproc_type_props(?)) a, " +
                        "table(sys_0001t.sel_type_props_type()) b, " +
                        "table(sys_0001t.sel_type_props_cat()) c, " +
                        "table(sys_0001t.sel_measure()) d " +
                    "where b.prop_val_type = a.prop_type and c.prop_cat_id = a.prop_cat and d.measure_id = a.prop_measure " +
            "union all " +
            "select a.techproc_prop_id as prop_id, a.prop_name, a.prop_type, a.prop_cat, " +
            "a.prop_def, a.prop_measure, a.sp_header_id, a.sp_header_name, b.prop_val_type_name, null, " +
            "d.measure_name, d.short_name " +
                "from table(sys_0001t.sel_techproc_type_props(?)) a, " +
                        "table(sys_0001t.sel_type_props_type()) b, " +
                        "table(sys_0001t.sel_measure()) d " +
                    "where a.prop_cat = 'S' and b.prop_val_type = a.prop_type and d.measure_id = a.prop_measure";

    private static final String ADD_STRUCT_TYPE = "{? = call sys_0001t.add_techproc_type(?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE = "{? = call sys_0001t.del_techproc_type(?, ?, ?, ?)}";
    private static final String ADD_STRUCT_TYPE_PROP = "{? = call sys_0001t.add_techproc_type_prop(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String REMOVE_STRUCT_TYPE_PROP = "{? = call sys_0001t.del_techproc_type_prop(?, ?, ?, ?, ?)}";

    private static final String MOVE_PROP_UP = "{? = call sys_0001t.set_techproc_type_prop_up(?, ?)}";
    private static final String MOVE_PROP_DOWN = "{? = call sys_0001t.set_techproc_type_prop_down(?, ?)}";

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
        return wrapperDivisions.getStructTypeProps(id, SELECT_STRUCT_TYPE_PROPS);
    }

    @Override
    public void moveProp(int typeID, int propID, int oldPosition, int newPosition) throws SystemParamException {
        wrapperDivisions.moveProp(typeID, propID, oldPosition, newPosition, MOVE_PROP_UP, MOVE_PROP_DOWN);
    }
}
