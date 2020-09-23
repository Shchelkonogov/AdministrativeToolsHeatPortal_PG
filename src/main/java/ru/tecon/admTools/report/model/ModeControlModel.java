package ru.tecon.admTools.report.model;

import java.util.StringJoiner;

/**
 * Модель данных для отчета "Контроль режима"
 */
public class ModeControlModel {

    private String objectName;
    private String address;
    private String graph;
    private String decrease;
    private String tMin;
    private String tMax;
    private String aMin;
    private String aMax;
    private String agrName;

    public ModeControlModel(String objectName, String address, String graph, String decrease, String tMin,
                            String tMax, String aMin, String aMax, String agrName) {
        this.objectName = objectName;
        this.address = address;
        this.graph = graph;
        this.decrease = decrease;
        this.tMin = tMin;
        this.tMax = tMax;
        this.aMin = aMin;
        this.aMax = aMax;
        this.agrName = agrName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getAddress() {
        return address;
    }

    public String getGraph() {
        return graph;
    }

    public String getDecrease() {
        return decrease;
    }

    public String gettMin() {
        return tMin;
    }

    public String gettMax() {
        return tMax;
    }

    public String getaMin() {
        return aMin;
    }

    public String getaMax() {
        return aMax;
    }

    public String getAgrName() {
        return agrName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ModeControlModel.class.getSimpleName() + "[", "]")
                .add("objectName='" + objectName + "'")
                .add("address='" + address + "'")
                .add("graph='" + graph + "'")
                .add("decrease='" + decrease + "'")
                .add("tMin='" + tMin + "'")
                .add("tMax='" + tMax + "'")
                .add("aMin='" + aMin + "'")
                .add("aMax='" + aMax + "'")
                .add("agrName='" + agrName + "'")
                .toString();
    }
}
