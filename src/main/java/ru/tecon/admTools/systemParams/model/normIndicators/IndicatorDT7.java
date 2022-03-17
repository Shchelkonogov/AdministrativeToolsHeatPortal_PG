package ru.tecon.admTools.systemParams.model.normIndicators;

import java.util.StringJoiner;

/**
 * Класс для описания нормативных показателе ΔT7
 * @author Maksim Shchelkonogov
 */
public class IndicatorDT7 {

    private double dtMin;
    private double dtMax;
    private double dtNorm;

    private boolean change = false;

    public IndicatorDT7(double dtMin, double dtMax, double dtNorm) {
        this.dtMin = dtMin;
        this.dtMax = dtMax;
        this.dtNorm = dtNorm;
    }

    public double getDtMin() {
        return dtMin;
    }

    public void setDtMin(double dtMin) {
        this.change = true;
        this.dtMin = dtMin;
    }

    public double getDtMax() {
        return dtMax;
    }

    public void setDtMax(double dtMax) {
        this.change = true;
        this.dtMax = dtMax;
    }

    public double getDtNorm() {
        return dtNorm;
    }

    public void setDtNorm(double dtNorm) {
        this.change = true;
        this.dtNorm = dtNorm;
    }

    public boolean isChange() {
        return change;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndicatorDT7.class.getSimpleName() + "[", "]")
                .add("dtMin=" + dtMin)
                .add("dtMax=" + dtMax)
                .add("dtNorm=" + dtNorm)
                .toString();
    }
}
