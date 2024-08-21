package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.08.2024
 */
public class IndicatorBorderGvs {

    private double kdt;
    private double kdto;
    private double ky;
    private double dt;
    private double dt7;
    private double t7;
    private double kgvs;

    private boolean change = false;

    public IndicatorBorderGvs(double kdt, double kdto, double ky, double dt, double dt7, double t7, double kgvs) {
        this.kdt = kdt;
        this.kdto = kdto;
        this.ky = ky;
        this.dt = dt;
        this.dt7 = dt7;
        this.t7 = t7;
        this.kgvs = kgvs;
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

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        change = true;
        this.dt = dt;
    }

    public double getDt7() {
        return dt7;
    }

    public void setDt7(double dt7) {
        change = true;
        this.dt7 = dt7;
    }

    public double getT7() {
        return t7;
    }

    public void setT7(double t7) {
        change = true;
        this.t7 = t7;
    }

    public double getKgvs() {
        return kgvs;
    }

    public void setKgvs(double kgvs) {
        change = true;
        this.kgvs = kgvs;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorBorderGvs.class.getSimpleName() + "[", "]")
                .add("kdt=" + kdt)
                .add("kdto=" + kdto)
                .add("ky=" + ky)
                .add("dt=" + dt)
                .add("dt7=" + dt7)
                .add("t7=" + t7)
                .add("kgvs=" + kgvs)
                .toString();
    }
}
