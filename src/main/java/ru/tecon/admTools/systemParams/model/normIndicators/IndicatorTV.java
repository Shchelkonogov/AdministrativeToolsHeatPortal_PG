package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий нормативные показатели теплового ввода
 * @author Maksim Shchelkonogov
 */
public class IndicatorTV {

    private double kpn;
    private double kz;
    private double kp;

    private boolean change = false;

    public IndicatorTV(double kpn, double kz, double kp) {
        this.kpn = kpn;
        this.kz = kz;
        this.kp = kp;
    }

    public double getKpn() {
        return kpn;
    }

    public void setKpn(double kpn) {
        change = true;
        this.kpn = kpn;
    }

    public double getKz() {
        return kz;
    }

    public void setKz(double kz) {
        change = true;
        this.kz = kz;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        change = true;
        this.kp = kp;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorTV.class.getSimpleName() + "[", "]")
                .add("kpn='" + kpn + "'")
                .add("kz='" + kz + "'")
                .add("kp='" + kp + "'")
                .toString();
    }
}
