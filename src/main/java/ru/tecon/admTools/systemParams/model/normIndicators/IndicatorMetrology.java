package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 20.08.2024
 */
public class IndicatorMetrology {

    private double kt;
    private double kp;
    private double kgKv;
    private double kq;

    private boolean change = false;

    public IndicatorMetrology(double kt, double kp, double kgKv, double kq) {
        this.kt = kt;
        this.kp = kp;
        this.kgKv = kgKv;
        this.kq = kq;
    }

    public double getKt() {
        return kt;
    }

    public void setKt(double kt) {
        change = true;
        this.kt = kt;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        change = true;
        this.kp = kp;
    }

    public double getKgKv() {
        return kgKv;
    }

    public void setKgKv(double kgKv) {
        change = true;
        this.kgKv = kgKv;
    }

    public double getKq() {
        return kq;
    }

    public void setKq(double kq) {
        change = true;
        this.kq = kq;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorMetrology.class.getSimpleName() + "[", "]")
                .add("kt=" + kt)
                .add("kp=" + kp)
                .add("kgKv=" + kgKv)
                .add("kq=" + kq)
                .toString();
    }
}
