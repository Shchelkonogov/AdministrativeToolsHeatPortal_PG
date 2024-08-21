package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.08.2024
 */
public class IndicatorBorderVent {

    private double kdt;
    private double kdto;
    private double ky;
    private double kdg;

    private boolean change = false;

    public IndicatorBorderVent(double kdt, double kdto, double ky, double kdg) {
        this.kdt = kdt;
        this.kdto = kdto;
        this.ky = ky;
        this.kdg = kdg;
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

    public double getKdg() {
        return kdg;
    }

    public void setKdg(double kdg) {
        change = true;
        this.kdg = kdg;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorBorderVent.class.getSimpleName() + "[", "]")
                .add("kdt=" + kdt)
                .add("kdto=" + kdto)
                .add("ky=" + ky)
                .add("kdg=" + kdg)
                .toString();
    }
}
