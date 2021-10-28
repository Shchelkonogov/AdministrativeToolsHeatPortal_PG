package ru.tecon.admTools.systemParams.model;

import java.util.StringJoiner;

/**
 * Класс для данных по коэффициентам режимной карты
 * @author Maksim Shchelkonogov
 */
public class CoefficientRC {

    private int id;
    private String code;
    private String name;
    private double tMin;
    private double tMax;
    private boolean t;
    private double aMin;
    private double aMax;
    private boolean a;

    private boolean change = false;

    public CoefficientRC(int id, String code, String name, double tMin, double tMax, boolean t, double aMin, double aMax, boolean a) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.tMin = tMin;
        this.tMax = tMax;
        this.t = t;
        this.aMin = aMin;
        this.aMax = aMax;
        this.a = a;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double gettMin() {
        return tMin;
    }

    public double gettMax() {
        return tMax;
    }

    public boolean isT() {
        return t;
    }

    public double getaMin() {
        return aMin;
    }

    public double getaMax() {
        return aMax;
    }

    public boolean isA() {
        return a;
    }

    public void settMin(double tMin) {
        change = true;
        this.tMin = tMin;
    }

    public void settMax(double tMax) {
        change = true;
        this.tMax = tMax;
    }

    public void setT(boolean t) {
        this.t = t;
    }

    public void setaMin(double aMin) {
        change = true;
        this.aMin = aMin;
    }

    public void setaMax(double aMax) {
        change = true;
        this.aMax = aMax;
    }

    public void setA(boolean a) {
        this.a = a;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoefficientRC.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("code='" + code + "'")
                .add("name='" + name + "'")
                .add("tMin=" + tMin)
                .add("tMax=" + tMax)
                .add("t=" + t)
                .add("aMin=" + aMin)
                .add("aMax=" + aMax)
                .add("a=" + a)
                .toString();
    }
}
