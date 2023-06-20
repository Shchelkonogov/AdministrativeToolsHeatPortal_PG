package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.temperature.Temperature;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру параметров обобщенной модели
 * @author Aleksey Sergeev
 */
public class Param implements Serializable {

    private long id;
    private long paramTypeId;
    private String parCode;
    private String parMemo;
    private String parName;
    private long techprocTypeId;
    private short zone;
    private Temperature isGraph;
    private long visible;
    private String calc;
    private Temperature isDecrease;
    private boolean editEnable;
    private boolean letoControl;

    public Param() {
    }

    public Param(long id, long paramTypeId, String parCode, String parMemo, String parName, long techprocTypeId, short zone,
                 Temperature isGraph, long visible, String calc, Temperature isDecrease, boolean editEnable, boolean letoControl) {
        this.id = id;
        this.paramTypeId = paramTypeId;
        this.parCode = parCode;
        this.parMemo = parMemo;
        this.parName = parName;
        this.techprocTypeId = techprocTypeId;
        this.zone = zone;
        this.isGraph = isGraph;
        this.visible = visible;
        this.calc = calc;
        this.isDecrease = isDecrease;
        this.editEnable = editEnable;
        this.letoControl = letoControl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParCode() {
        return parCode;
    }

    public void setParCode(String parCode) {
        this.parCode = parCode;
    }

    public String getParMemo() {
        return parMemo;
    }

    public void setParMemo(String parMemo) {
        this.parMemo = parMemo;
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    public short getZone() {
        return zone;
    }

    public void setZone(short zone) {
        this.zone = zone;
    }

    public Temperature getIsGraph() {
        return isGraph;
    }

    public void setIsGraph(Temperature isGraph) {
        this.isGraph = isGraph;
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

    public Temperature getIsDecrease() {
        return isDecrease;
    }

    public void setIsDecrease(Temperature isDecrease) {
        this.isDecrease = isDecrease;
    }

    public boolean getEditEnable() {
        return editEnable;
    }

    public short getEditEnableShort() {

        if (editEnable){
            return 1;
        } else {
            return 0;
        }
    }

    public void setEditEnable(boolean editEnable) {
        this.editEnable = editEnable;
    }

    public boolean getLetoControl() {
        return letoControl;
    }

    public short getLetoControlShort(){
        if (letoControl){
            return 1;
        } else {
            return 0;
        }
    }

    public void setLetoControl(boolean letoControl) {
        this.letoControl = letoControl;
    }

    public long getParamTypeId() {
        return paramTypeId;
    }

    public long getTechprocTypeId() {
        return techprocTypeId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Param.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("paramTypeId=" + paramTypeId)
                .add("parCode='" + parCode + "'")
                .add("parMemo='" + parMemo + "'")
                .add("parName='" + parName + "'")
                .add("techprocTypeId=" + techprocTypeId)
                .add("zone=" + zone)
                .add("isGraph=" + isGraph)
                .add("visible=" + visible)
                .add("calc='" + calc + "'")
                .add("isDecrease=" + isDecrease)
                .add("editEnable=" + editEnable)
                .add("letoControl=" + letoControl)
                .toString();
    }
}
