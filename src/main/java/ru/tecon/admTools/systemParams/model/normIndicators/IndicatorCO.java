package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий нормативные показатели центрального отопления
 * @author Maksim Shchelkonogov
 */
public class IndicatorCO {

    private double id;
    private String name;
    private double kz;
    private double kp;

    private boolean change = false;

    public IndicatorCO(double id, String name, double kz, double kp) {
        this.id = id;
        this.name = name;
        this.kz = kz;
        this.kp = kp;
    }

    public double getId() {
        return id;
    }

    public double getKz() {
        return kz;
    }

    public void setKz(double kz) {
        change = true;
        this.kz = kz;
    }

    public String getName() {
        return name;
    }

    public boolean isChange() {
        return change;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        change = true;
        this.kp = kp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorCO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("kz=" + kz)
                .add("kp=" + kp)
                .add("change=" + change)
                .toString();
    }
}
