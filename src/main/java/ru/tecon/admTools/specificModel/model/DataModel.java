package ru.tecon.admTools.specificModel.model;

import ru.tecon.admTools.specificModel.model.additionalModel.Additional;

import java.io.Serializable;
import java.util.StringJoiner;

public class DataModel implements Serializable {

    private final int parID;
    private final int statAgr;

    private String parName;
    private String parMemo;
    private String statAgrName;
    private String zone;
    private String techProcCode;
    private String measureName;
    private String paramTypeName;

    private Additional additionalData;

    private boolean change = false;
    private boolean tempGraphRender = false;
    private boolean optValuesRender = false;
    private boolean decreaseValueRender = false;

    public DataModel(int parID, int statAgr) {
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

    public String getId() {
        return parID + "_" + statAgr;
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

    public boolean isTempGraphRender() {
        return tempGraphRender;
    }

    public void setTempGraphRender(boolean tempGraphRender) {
        this.tempGraphRender = tempGraphRender;
    }

    public boolean isOptValuesRender() {
        return optValuesRender;
    }

    public void setOptValuesRender(boolean optValuesRender) {
        this.optValuesRender = optValuesRender;
    }

    public boolean isDecreaseValueRender() {
        return decreaseValueRender;
    }

    public void setDecreaseValueRender(boolean decreaseValueRender) {
        this.decreaseValueRender = decreaseValueRender;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataModel.class.getSimpleName() + "[", "]")
                .add("id=" + getId())
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
                .add("tempGraphRender=" + tempGraphRender)
                .add("optValuesRender=" + optValuesRender)
                .add("decreaseValueRender=" + decreaseValueRender)
                .toString();
    }
}
