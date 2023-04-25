package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру свойств аналогового стат. агрегата параметра обобщенной модели
 * @author Aleksey Sergeev
 */
public class ParamProp implements Serializable {

    private long par_id;
    private long param_type_id;
    private long stat_agr_id;
    private long prop_id;
    private String prop_name;
    private String prop_val_def;
    private long prop_cond_great;
    private String prop_cond_great_name;
    private long prop_cond_less;
    private String prop_cond_less_name;

    public ParamProp() {
    }

    public ParamProp(long par_id, long param_type_id, long stat_agr_id, long prop_id, String prop_name, String prop_val_def, long prop_cond_great, String prop_cond_great_name, long prop_cond_less, String prop_cond_less_name) {
        this.par_id = par_id;
        this.param_type_id = param_type_id;
        this.stat_agr_id = stat_agr_id;
        this.prop_id = prop_id;
        this.prop_name = prop_name;
        this.prop_val_def = prop_val_def;
        this.prop_cond_great = prop_cond_great;
        this.prop_cond_great_name = prop_cond_great_name;
        this.prop_cond_less = prop_cond_less;
        this.prop_cond_less_name = prop_cond_less_name;
    }

    public long getPar_id() {
        return par_id;
    }

    public void setPar_id(long par_id) {
        this.par_id = par_id;
    }

    public long getStat_agr_id() {
        return stat_agr_id;
    }

    public long getProp_id() {
        return prop_id;
    }

    public void setProp_id(long prop_id) {
        this.prop_id = prop_id;
    }

    public String getProp_name() {
        return prop_name;
    }

    public void setProp_name(String prop_name) {
        this.prop_name = prop_name;
    }

    public String getProp_val_def() {
        return prop_val_def;
    }

    public void setProp_val_def(String prop_val_def) {
        this.prop_val_def = prop_val_def;
    }

    public String getProp_cond_great_name() {
        return prop_cond_great_name;
    }

    public void setProp_cond_great_name(String prop_cond_great_name) {
        this.prop_cond_great_name = prop_cond_great_name;
    }

    public String getProp_cond_less_name() {
        return prop_cond_less_name;
    }

    public void setProp_cond_less_name(String prop_cond_less_name) {
        this.prop_cond_less_name = prop_cond_less_name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamProp.class.getSimpleName() + "[", "]")
                .add("par_id=" + par_id)
                .add("param_type_id=" + param_type_id)
                .add("stat_agr_id=" + stat_agr_id)
                .add("prop_id=" + prop_id)
                .add("prop_name='" + prop_name + "'")
                .add("prop_val_def='" + prop_val_def + "'")
                .add("prop_cond_great=" + prop_cond_great)
                .add("prop_cond_great_name='" + prop_cond_great_name + "'")
                .add("prop_cond_less=" + prop_cond_less)
                .add("prop_cond_less_name='" + prop_cond_less_name + "'")
                .toString();
    }
}
