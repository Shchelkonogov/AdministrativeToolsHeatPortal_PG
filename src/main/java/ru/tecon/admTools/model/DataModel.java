package ru.tecon.admTools.model;

import ru.tecon.admTools.validation.DoubleValue;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public class DataModel {

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

    private Integer decreaseID;
    private String decreaseName;
    private boolean decreaseDisable;

    private Integer graphID;
    private String graphName;
    private boolean graphDisable;

    @DoubleValue()
    private Double tMin;
    private boolean tMinDisable;
    @DoubleValue
    private Double tMax;
    private boolean tMaxDisable;
    private boolean tPercent;

    @DoubleValue
    private Double aMin;
    private boolean aMinDisable;
    @DoubleValue
    private Double aMax;
    private boolean aMaxDisable;
    private boolean aPercent;

    private boolean absolute;

    private boolean color;

    private List<ParamConditionDataModel> paramConditions;

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

    public String getDecreaseName() {
        return decreaseName;
    }

    public void setDecreaseName(String decreaseName) {
        this.decreaseName = decreaseName;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void setAbsolute(boolean absolute) {
        if (!absolute) {
            setaPercent(false);
        }
        this.absolute = absolute;
    }

    public Double gettMin() {
        return tMin;
    }

    public void settMin(Double tMin) {
        this.tMin = tMin;
    }

    public Double gettMax() {
        return tMax;
    }

    public void settMax(Double tMax) {
        this.tMax = tMax;
    }

    public Double getaMin() {
        return aMin;
    }

    public void setaMin(Double aMin) {
        this.aMin = aMin;
    }

    public Double getaMax() {
        return aMax;
    }

    public void setaMax(Double aMax) {
        this.aMax = aMax;
    }

    public boolean istPercent() {
        return tPercent;
    }

    public void settPercent(boolean tPercent) {
        this.tPercent = tPercent;
    }

    public boolean isaPercent() {
        return aPercent;
    }

    public void setaPercent(boolean aPercent) {
        if (aPercent) {
            setAbsolute(true);
        }
        this.aPercent = aPercent;
    }

    public boolean isaMaxDisable() {
        return aMaxDisable;
    }

    public void setaMaxDisable(boolean aMaxDisable) {
        this.aMaxDisable = aMaxDisable;
    }

    public String getaMaxColor() {
        return isaMaxDisable() ? " gray" : "";
    }

    public boolean isaMinDisable() {
        return aMinDisable;
    }

    public void setaMinDisable(boolean aMinDisable) {
        this.aMinDisable = aMinDisable;
    }

    public String getaMinColor() {
        return isaMinDisable() ? " gray" : "";
    }

    public boolean isaPersentDisable() {
        return isaMaxDisable() && isaMinDisable();
    }

    public boolean istMinDisable() {
        return tMinDisable;
    }

    public void settMinDisable(boolean tMinDisable) {
        this.tMinDisable = tMinDisable;
    }

    public String gettMinColor() {
        return istMinDisable() ? " gray" : "";
    }

    public boolean istMaxDisable() {
        return tMaxDisable;
    }

    public void settMaxDisable(boolean tMaxDisable) {
        this.tMaxDisable = tMaxDisable;
    }

    public String gettMaxColor() {
        return istMaxDisable() ? " gray" : "";
    }

    public boolean istPersentDisable() {
        return istMaxDisable() && istMinDisable();
    }

    public String getColor() {
        return color ? " red" : "";
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean isDecreaseDisable() {
        return decreaseDisable;
    }

    public void setDecreaseDisable(boolean decreaseDisable) {
        this.decreaseDisable = decreaseDisable;
    }

    public boolean isGraphDisable() {
        return graphDisable;
    }

    public void setGraphDisable(boolean graphDisable) {
        this.graphDisable = graphDisable;
    }

    public String getGraphColor() {
        return isGraphDisable() ? " gray" : "";
    }

    public String getDecreaseColor() {
        return isDecreaseDisable() ? " gray" : "";
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    public List<ParamConditionDataModel> getParamConditions() {
        return paramConditions;
    }

    public void setParamConditions(List<ParamConditionDataModel> paramConditions) {
        this.paramConditions = paramConditions;
    }

    public Integer getDecreaseID() {
        return decreaseID;
    }

    public void setDecreaseID(Integer decreaseID) {
        this.decreaseID = decreaseID;
    }

    public int getId() {
        return id;
    }

    public Integer getGraphID() {
        return graphID;
    }

    public void setGraphID(Integer graphID) {
        this.graphID = graphID;
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
                .add("decreaseID=" + decreaseID)
                .add("decreaseName='" + decreaseName + "'")
                .add("decreaseDisable=" + decreaseDisable)
                .add("graphID=" + graphID)
                .add("graphName='" + graphName + "'")
                .add("graphDisable=" + graphDisable)
                .add("tMin='" + tMin + "'")
                .add("tMinDisable=" + tMinDisable)
                .add("tMax='" + tMax + "'")
                .add("tMaxDisable=" + tMaxDisable)
                .add("tPercent=" + tPercent)
                .add("aMin='" + aMin + "'")
                .add("aMinDisable=" + aMinDisable)
                .add("aMax='" + aMax + "'")
                .add("aMaxDisable=" + aMaxDisable)
                .add("aPercent=" + aPercent)
                .add("absolute=" + absolute)
                .add("color=" + color)
                .add("paramConditions=" + paramConditions)
                .toString();
    }
}
