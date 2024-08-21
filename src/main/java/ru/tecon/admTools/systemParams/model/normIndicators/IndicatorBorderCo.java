package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 20.08.2024
 */
public class IndicatorBorderCo {

    private double kdt;
    private double kdto;
    private double ky;
    private double kdq;
    private double k;
    private double k1;
    private double k2;

    private boolean change = false;

    public IndicatorBorderCo(double kdt, double kdto, double ky, double kdq, double k, double k1, double k2) {
        this.kdt = kdt;
        this.kdto = kdto;
        this.ky = ky;
        this.kdq = kdq;
        this.k = k;
        this.k1 = k1;
        this.k2 = k2;
    }

    public double getKdt() {
        return kdt;
    }

    public void setKdt(double kdt) {
        change = true;
        this.kdt = kdt;
    }

    public double getKdto() {
        return kdto;
    }

    public void setKdto(double kdto) {
        change = true;
        this.kdto = kdto;
    }

    public double getKy() {
        return ky;
    }

    public void setKy(double ky) {
        change = true;
        this.ky = ky;
    }

    public double getKdq() {
        return kdq;
    }

    public void setKdq(double kdq) {
        change = true;
        this.kdq = kdq;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        change = true;
        this.k = k;
    }

    public double getK1() {
        return k1;
    }

    public void setK1(double k1) {
        change = true;
        this.k1 = k1;
    }

    public double getK2() {
        return k2;
    }

    public void setK2(double k2) {
        change = true;
        this.k2 = k2;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorBorderCo.class.getSimpleName() + "[", "]")
                .add("kdt=" + kdt)
                .add("kdto=" + kdto)
                .add("ky=" + ky)
                .add("kdq=" + kdq)
                .add("k=" + k)
                .add("k1=" + k1)
                .add("k2=" + k2)
                .toString();
    }
}
