package ru.tecon.admTools.systemParams.model.groundTemp;

import java.util.StringJoiner;

/**
 * Класс описывающий эталонный значения температуры
 * @author Maksim Shchelkonogov
 */
public class ReferenceValue {

    private double t3;
    private double t4;
    private double t7;
    private double t13;
    private double tgr;

    private boolean change = false;

    public ReferenceValue(double t3, double t4, double t7, double t13, double tgr) {
        this.t3 = t3;
        this.t4 = t4;
        this.t7 = t7;
        this.t13 = t13;
        this.tgr = tgr;
    }

    public double getT3() {
        return t3;
    }

    public void setT3(double t3) {
        this.change = true;
        this.t3 = t3;
    }

    public double getT4() {
        return t4;
    }

    public void setT4(double t4) {
        this.change = true;
        this.t4 = t4;
    }

    public double getT7() {
        return t7;
    }

    public void setT7(double t7) {
        this.change = true;
        this.t7 = t7;
    }

    public double getT13() {
        return t13;
    }

    public void setT13(double t13) {
        this.change = true;
        this.t13 = t13;
    }

    public double getTgr() {
        return tgr;
    }

    public void setTgr(double tgr) {
        this.change = true;
        this.tgr = tgr;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReferenceValue.class.getSimpleName() + "[", "]")
                .add("t3=" + t3)
                .add("t4=" + t4)
                .add("t7=" + t7)
                .add("t13=" + t13)
                .add("tgr=" + tgr)
                .add("change=" + change)
                .toString();
    }
}
