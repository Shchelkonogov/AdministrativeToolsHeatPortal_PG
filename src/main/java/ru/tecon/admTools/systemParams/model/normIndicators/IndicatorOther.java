package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс описывающий общие нормативные показатели
 * @author Maksim Shchelkonogov
 */
public class IndicatorOther {

    private double t7;
    private double dt7;
    private double kt;
    private double kto;
    private double kg;
    private double kv;
    private double kuco;
    private double kuvent;
    private double kugvs;
    private double kpco;
    private double kdgco;
    private double k1min;
    private double k1max;
    private double k2min;
    private double k2max;
    private double kpgvs;
    private double kdggvs;
    private double kqgvsl;
    private double gvsdt;
    private double kgvsc;

    private boolean change = false;

    public IndicatorOther(double t7, double dt7, double kt, double kto, double kg, double kv, double kuco,
                          double kuvent, double kugvs, double kpco, double kdgco, double k1min, double k1max,
                          double k2min, double k2max, double kpgvs, double kdggvs, double kqgvsl, double gvsdt,
                          double kgvsc) {
        this.t7 = t7;
        this.dt7 = dt7;
        this.kt = kt;
        this.kto = kto;
        this.kg = kg;
        this.kv = kv;
        this.kuco = kuco;
        this.kuvent = kuvent;
        this.kugvs = kugvs;
        this.kpco = kpco;
        this.kdgco = kdgco;
        this.k1min = k1min;
        this.k1max = k1max;
        this.k2min = k2min;
        this.k2max = k2max;
        this.kpgvs = kpgvs;
        this.kdggvs = kdggvs;
        this.kqgvsl = kqgvsl;
        this.gvsdt = gvsdt;
        this.kgvsc = kgvsc;
    }

    public double getT7() {
        return t7;
    }

    public void setT7(double t7) {
        this.change = true;
        this.t7 = t7;
    }

    public double getDt7() {
        return dt7;
    }

    public void setDt7(double dt7) {
        this.change = true;
        this.dt7 = dt7;
    }

    public double getKt() {
        return kt;
    }

    public void setKt(double kt) {
        this.change = true;
        this.kt = kt;
    }

    public double getKto() {
        return kto;
    }

    public void setKto(double kto) {
        this.change = true;
        this.kto = kto;
    }

    public double getKg() {
        return kg;
    }

    public void setKg(double kg) {
        this.change = true;
        this.kg = kg;
    }

    public double getKv() {
        return kv;
    }

    public void setKv(double kv) {
        this.change = true;
        this.kv = kv;
    }

    public double getKuco() {
        return kuco;
    }

    public void setKuco(double kuco) {
        this.change = true;
        this.kuco = kuco;
    }

    public double getKuvent() {
        return kuvent;
    }

    public void setKuvent(double kuvent) {
        this.change = true;
        this.kuvent = kuvent;
    }

    public double getKugvs() {
        return kugvs;
    }

    public void setKugvs(double kugvs) {
        this.change = true;
        this.kugvs = kugvs;
    }

    public double getKpco() {
        return kpco;
    }

    public void setKpco(double kpco) {
        this.change = true;
        this.kpco = kpco;
    }

    public double getKdgco() {
        return kdgco;
    }

    public void setKdgco(double kdgco) {
        this.change = true;
        this.kdgco = kdgco;
    }

    public double getK1min() {
        return k1min;
    }

    public void setK1min(double k1min) {
        this.change = true;
        this.k1min = k1min;
    }

    public double getK1max() {
        return k1max;
    }

    public void setK1max(double k1max) {
        this.change = true;
        this.k1max = k1max;
    }

    public double getK2min() {
        return k2min;
    }

    public void setK2min(double k2min) {
        this.change = true;
        this.k2min = k2min;
    }

    public double getK2max() {
        return k2max;
    }

    public void setK2max(double k2max) {
        this.change = true;
        this.k2max = k2max;
    }

    public double getKpgvs() {
        return kpgvs;
    }

    public void setKpgvs(double kpgvs) {
        this.change = true;
        this.kpgvs = kpgvs;
    }

    public double getKdggvs() {
        return kdggvs;
    }

    public void setKdggvs(double kdggvs) {
        this.change = true;
        this.kdggvs = kdggvs;
    }

    public double getKqgvsl() {
        return kqgvsl;
    }

    public void setKqgvsl(double kqgvsl) {
        this.change = true;
        this.kqgvsl = kqgvsl;
    }

    public double getGvsdt() {
        return gvsdt;
    }

    public void setGvsdt(double gvsdt) {
        this.change = true;
        this.gvsdt = gvsdt;
    }

    public double getKgvsc() {
        return kgvsc;
    }

    public void setKgvsc(double kgvsc) {
        this.change = true;
        this.kgvsc = kgvsc;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorOther.class.getSimpleName() + "[", "]")
                .add("t7=" + t7)
                .add("dt7=" + dt7)
                .add("kt=" + kt)
                .add("kg=" + kg)
                .add("kv=" + kv)
                .add("kuco=" + kuco)
                .add("kuvent=" + kuvent)
                .add("kugvs=" + kugvs)
                .add("kpco=" + kpco)
                .add("kdgco=" + kdgco)
                .add("k1min=" + k1min)
                .add("k1max=" + k1max)
                .add("k2min=" + k2min)
                .add("k2max=" + k2max)
                .add("kpgvs=" + kpgvs)
                .add("kdggvs=" + kdggvs)
                .add("kqgvsl=" + kqgvsl)
                .add("gvsdt=" + gvsdt)
                .add("kgvsc=" + kgvsc)
                .add("change=" + change)
                .toString();
    }
}
