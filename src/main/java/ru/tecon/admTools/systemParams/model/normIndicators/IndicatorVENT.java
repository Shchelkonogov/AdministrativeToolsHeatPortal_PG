package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий нормативные показатели вентиляции
 * @author Maksim Shchelkonogov
 */
public class IndicatorVENT {

    private double id;
    private String name;
    private double kz;
    private double kp;

    private boolean change = false;

    public IndicatorVENT(double id, String name, double kz, double kp) {
        this.id = id;
        this.name = name;
        this.kz = kz;
        this.kp = kp;
    }

    public double getKz() {
        return kz;
    }

    public void setKz(double kz) {
        this.change = true;
        this.kz = kz;
    }

    public double getId() {
        return id;
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
        this.change = true;
        this.kp = kp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorVENT.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("kz=" + kz)
                .add("kp=" + kp)
                .add("change=" + change)
                .toString();
    }
}
