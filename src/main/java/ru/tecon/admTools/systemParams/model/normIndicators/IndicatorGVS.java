package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий нормативные показатели горячего водоснабжения
 * @author Maksim Shchelkonogov
 */
public class IndicatorGVS {

    private int id;
    private String name;
    private double kc;

    private boolean change = false;

    public IndicatorGVS(int id, String name, double kc) {
        this.id = id;
        this.name = name;
        this.kc = kc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getKc() {
        return kc;
    }

    public void setKc(double kc) {
        this.change = true;
        this.kc = kc;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorGVS.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("kc=" + kc)
                .add("change=" + change)
                .toString();
    }
}
