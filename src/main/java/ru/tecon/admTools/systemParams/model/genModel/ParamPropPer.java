package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру перечислений перечислимого стат. агрегата параметра обобщенной модели
 * @author Aleksey Sergeev
 */
public class ParamPropPer implements Serializable {
    private long par_id;
    private long param_type_id;
    private long stat_agr_id;
    private long enum_code;
    private String prop_val;
    private long prop_cond;

    public ParamPropPer() {
    }

    public ParamPropPer(long par_id, long param_type_id, long stat_agr_id, long enum_code, String prop_val, long prop_cond) {
        this.par_id = par_id;
        this.param_type_id = param_type_id;
        this.stat_agr_id = stat_agr_id;
        this.enum_code = enum_code;
        this.prop_val = prop_val;
        this.prop_cond = prop_cond;
    }

    public long getPar_id() {
        return par_id;
    }

    public long getStat_agr_id() {
        return stat_agr_id;
    }

    public long getEnum_code() {
        return enum_code;
    }

    public void setEnum_code(long enum_code) {
        this.enum_code = enum_code;
    }

    public String getProp_val() {
        return prop_val;
    }

    public void setProp_val(String prop_val) {
        this.prop_val = prop_val;
    }

    public long getProp_cond() {
        return prop_cond;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamPropPer.class.getSimpleName() + "[", "]")
                .add("par_id=" + par_id)
                .add("param_type_id=" + param_type_id)
                .add("stat_agr_id=" + stat_agr_id)
                .add("enum_code=" + enum_code)
                .add("prop_val='" + prop_val + "'")
                .add("prop_cond=" + prop_cond)
                .toString();
    }
}
