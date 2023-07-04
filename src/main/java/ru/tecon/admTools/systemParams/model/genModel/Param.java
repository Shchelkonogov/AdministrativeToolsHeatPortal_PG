package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.temperature.Temperature;
import ru.tecon.admTools.systemParams.model.temperature.TemperatureStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру параметров обобщенной модели
 *
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
    private long visible;
    private String calc;
    private boolean editEnable;
    private boolean letoControl;

    private BigDecimal optValue;
    private Temperature temperature;
    private TemperatureStatus tempStatus = TemperatureStatus.EMPTY;

    public Param() {
    }

    public Param(long id, long paramTypeId, String parCode, String parMemo, String parName, long techprocTypeId, short zone,
                 long visible, String calc, boolean editEnable, boolean letoControl) {
        this();
        this.id = id;
        this.paramTypeId = paramTypeId;
        this.parCode = parCode;
        this.parMemo = parMemo;
        this.parName = parName;
        this.techprocTypeId = techprocTypeId;
        this.zone = zone;
        this.visible = visible;
        this.calc = calc;
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

    public Temperature getGraph() {
        return temperature;
    }

    public void setGraph(Temperature graph) {
        this.temperature = graph;
        tempStatus = TemperatureStatus.GRAPH;
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

    public Temperature getDecrease() {
        return temperature;
    }

    public void setDecrease(Temperature decrease) {
        this.temperature = decrease;
        tempStatus = TemperatureStatus.DAILY_REDUCTION;
    }

    public BigDecimal getOptValue() {
        return optValue;
    }

    public void setOptValue(BigDecimal optValue) {
        this.optValue = optValue;
        tempStatus = TemperatureStatus.OPT_VALUE;
    }

    public void setEditEnable(boolean editEnable) {
        this.editEnable = editEnable;
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

    public TemperatureStatus getTempStatus() {
        return tempStatus;
    }

    public boolean isEditEnable() {
        return editEnable;
    }

    public boolean isLetoControl() {
        return letoControl;
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
                .add("visible=" + visible)
                .add("calc='" + calc + "'")
                .add("editEnable=" + editEnable)
                .add("letoControl=" + letoControl)
                .add("optValue=" + optValue)
                .add("temperature=" + temperature)
                .add("tempStatus=" + tempStatus)
                .toString();
    }
}
