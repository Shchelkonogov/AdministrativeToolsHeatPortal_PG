package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру параметров обобщенной модели
 * @author Aleksey Sergeev
 */
public class Param implements Serializable {

    private long id;
    private long param_type_id;
    private String par_code;
    private String par_memo;
    private String par_name;
    private long techproc_type_id;
    private short zone;
    private Long is_graph;
    private long visible;
    private String calc;
    private Long is_decrease;
    private boolean edit_enable;
    private boolean leto_control;

    public Param() {
    }

    public Param(long id, long param_type_id, String par_code, String par_memo, String par_name, long techproc_type_id, short zone, Long is_graph, long visible, String calc, Long is_decrease, boolean edit_enable, boolean leto_control) {
        this.id = id;
        this.param_type_id = param_type_id;
        this.par_code = par_code;
        this.par_memo = par_memo;
        this.par_name = par_name;
        this.techproc_type_id = techproc_type_id;
        this.zone = zone;
        this.is_graph = is_graph;
        this.visible = visible;
        this.calc = calc;
        this.is_decrease = is_decrease;
        this.edit_enable = edit_enable;
        this.leto_control = leto_control;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPar_code() {
        return par_code;
    }

    public void setPar_code(String par_code) {
        this.par_code = par_code;
    }

    public String getPar_memo() {
        return par_memo;
    }

    public void setPar_memo(String par_memo) {
        this.par_memo = par_memo;
    }

    public String getPar_name() {
        return par_name;
    }

    public void setPar_name(String par_name) {
        this.par_name = par_name;
    }

    public short getZone() {
        return zone;
    }

    public void setZone(short zone) {
        this.zone = zone;
    }

    public Long getIs_graph() {
        return is_graph;
    }

    public void setIs_graph(Long is_graph) {
        this.is_graph = is_graph;
    }

    public long getVisible() {
        return visible;
    }

    public void setVisible(long visible) {
        this.visible = visible;
    }

    public String getCalc() {
        return calc;
    }

    public void setCalc(String calc) {
        this.calc = calc;
    }

    public Long getIs_decrease() {
        return is_decrease;
    }

    public void setIs_decrease(Long is_decrease) {
        this.is_decrease = is_decrease;
    }

    public boolean getEdit_enable() {
        return edit_enable;
    }

    public short getEdit_enableShort() {

        if (edit_enable){
            return 1;
        } else {
            return 0;
        }
    }

    public void setEdit_enable(boolean edit_enable) {
        this.edit_enable = edit_enable;
    }

    public boolean getLeto_control() {
        return leto_control;
    }

    public short getLeto_controlShort(){
        if (leto_control){
            return 1;
        } else {
            return 0;
        }
    }

    public void setLeto_control(boolean leto_control) {
        this.leto_control = leto_control;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Param.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("param_type_id=" + param_type_id)
                .add("par_code='" + par_code + "'")
                .add("par_memo='" + par_memo + "'")
                .add("par_name='" + par_name + "'")
                .add("techproc_type_id=" + techproc_type_id)
                .add("zone=" + zone)
                .add("is_graph=" + is_graph)
                .add("visible=" + visible)
                .add("calc='" + calc + "'")
                .add("is_decrease=" + is_decrease)
                .add("edit_enable=" + edit_enable)
                .add("leto_control=" + leto_control)
                .toString();
    }
}
