package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий нормативные показатели по недоотпуску
 * @author Maksim Shchelkonogov
 * 12.01.2023
 */
public class IndicatorUnderSupply {

    private double t3K1;
    private double t3K2;
    private double t3K3;
    private double t4K1;
    private double t4K2;
    private double t4K3;

    private boolean change = false;

    public IndicatorUnderSupply(double t3K1, double t3K2, double t3K3, double t4K1, double t4K2, double t4K3) {
        this.t3K1 = t3K1;
        this.t3K2 = t3K2;
        this.t3K3 = t3K3;
        this.t4K1 = t4K1;
        this.t4K2 = t4K2;
        this.t4K3 = t4K3;
    }

    public double getT3K1() {
        return t3K1;
    }

    public void setT3K1(double t3K1) {
        this.change = true;
        this.t3K1 = t3K1;
    }

    public double getT3K2() {
        return t3K2;
    }

    public void setT3K2(double t3K2) {
        this.change = true;
        this.t3K2 = t3K2;
    }

    public double getT3K3() {
        return t3K3;
    }

    public void setT3K3(double t3K3) {
        this.change = true;
        this.t3K3 = t3K3;
    }

    public double getT4K1() {
        return t4K1;
    }

    public void setT4K1(double t4K1) {
        this.change = true;
        this.t4K1 = t4K1;
    }

    public double getT4K2() {
        return t4K2;
    }

    public void setT4K2(double t4K2) {
        this.change = true;
        this.t4K2 = t4K2;
    }

    public double getT4K3() {
        return t4K3;
    }

    public void setT4K3(double t4K3) {
        this.change = true;
        this.t4K3 = t4K3;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorUnderSupply.class.getSimpleName() + "[", "]")
                .add("t3K1=" + t3K1)
                .add("t3K2=" + t3K2)
                .add("t3K3=" + t3K3)
                .add("t4K1=" + t4K1)
                .add("t4K2=" + t4K2)
                .add("t4K3=" + t4K3)
                .toString();
    }
}
