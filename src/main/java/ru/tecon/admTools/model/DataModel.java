package ru.tecon.admTools.model;

import ru.tecon.admTools.model.additionalModel.Additional;
import ru.tecon.admTools.model.additionalModel.AnalogData;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public class DataModel implements Serializable {

    private static AtomicInteger idGenerator = new AtomicInteger();

    private int id;

    private int parID;
    private int statAgr;

    private String parName;
    private String parMemo;
    private String statAgrName;
    private String zone;
    private String techProcCode;
    private String measureName;
    private String paramTypeName;

    private Additional additionalData;

    private boolean change = false;

    public DataModel() {
    }

    public DataModel(int parID, int statAgr) {
        this.id = idGenerator.getAndIncrement();
        this.parID = parID;
        this.statAgr = statAgr;
    }

    public int getParID() {
        return parID;
    }

    public int getStatAgr() {
        return statAgr;
    }

    public String getParMemo() {
        return parMemo;
    }

    public void setParMemo(String parMemo) {
        this.parMemo = parMemo;
    }

    public String getStatAgrName() {
        return statAgrName;
    }

    public void setStatAgrName(String statAgrName) {
        this.statAgrName = statAgrName;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTechProcCode() {
        return techProcCode;
    }

    public void setTechProcCode(String techProcCode) {
        this.techProcCode = techProcCode;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    public void setAdditionalData(Additional additionalData) {
        this.additionalData = additionalData;
    }

    public Additional getAdditionalData() {
        return additionalData;
    }

    public boolean isChange() {
        return change;
    }

    public int getId() {
        return id;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public String getParName() {
        return parName;
    }

    public String getParamTypeName() {
        return paramTypeName;
    }

    public void setParamTypeName(String paramTypeName) {
        this.paramTypeName = paramTypeName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataModel.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("parID=" + parID)
                .add("statAgr=" + statAgr)
                .add("parName='" + parName + "'")
                .add("parMemo='" + parMemo + "'")
                .add("statAgrName='" + statAgrName + "'")
                .add("zone='" + zone + "'")
                .add("techProcCode='" + techProcCode + "'")
                .add("measureName='" + measureName + "'")
                .add("paramTypeName='" + paramTypeName + "'")
                .add("additionalData=" + additionalData)
                .add("change=" + change)
                .toString();
    }
}
