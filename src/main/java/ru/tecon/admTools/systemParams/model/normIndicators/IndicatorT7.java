package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс для описания нормативных показателей T7
 * @author Maksim Shchelkonogov
 */
public class IndicatorT7 {

    private String id;
    private String name;
    private double minG;
    private double maxG;
    private double minP;
    private double maxP;

    private boolean change = false;

    public IndicatorT7(String id, String name, double minG, double maxG, double minP, double maxP) {
        this.id = id;
        this.name = name;
        this.minG = minG;
        this.maxG = maxG;
        this.minP = minP;
        this.maxP = maxP;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMinG() {
        return minG;
    }

    public void setMinG(double minG) {
        this.change = true;
        this.minG = minG;
    }

    public double getMaxG() {
        return maxG;
    }

    public void setMaxG(double maxG) {
        this.change = true;
        this.maxG = maxG;
    }

    public double getMinP() {
        return minP;
    }

    public void setMinP(double minP) {
        this.change = true;
        this.minP = minP;
    }

    public double getMaxP() {
        return maxP;
    }

    public void setMaxP(double maxP) {
        this.change = true;
        this.maxP = maxP;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorT7.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("minG=" + minG)
                .add("maxG=" + maxG)
                .add("minP=" + minP)
                .add("maxP=" + maxP)
                .toString();
    }
}
