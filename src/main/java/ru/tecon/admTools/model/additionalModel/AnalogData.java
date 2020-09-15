package ru.tecon.admTools.model.additionalModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Модель дополнительных данных для аналоговых параметров
 */
public class AnalogData implements Serializable, Additional {

    private GraphDecrease graph = new GraphDecrease();
    private GraphDecrease decrease = new GraphDecrease();

    private Borders t = new Borders();
    private Borders a = new Borders();

    private boolean absolute;
    private boolean color;

    public AnalogData() {

    }

    public void setGraph(Integer id, String name, boolean disable) {
        this.graph.id = id;
        this.graph.name = name;
        this.graph.disable = disable;
    }

    public void setDecrease(Integer id, String name, boolean disable) {
        this.decrease.id = id;
        this.decrease.name = name;
        this.decrease.disable = disable;
    }

    public void setT(Double min, Double max, boolean minDisable, boolean maxDisable, boolean percent) {
        this.t.min = min;
        this.t.max = max;
        this.t.minDisable = minDisable;
        this.t.maxDisable = maxDisable;
        this.t.percent = percent;
    }

    public void setA(Double min, Double max, boolean minDisable, boolean maxDisable, boolean percent) {
        this.a.min = min;
        this.a.max = max;
        this.a.minDisable = minDisable;
        this.a.maxDisable = maxDisable;
        this.a.percent = percent;
    }

    public void setAbsolute(boolean absolute) {
        if (!absolute) {
            setaPercent(false);
        }
        this.absolute = absolute;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public Integer getGraphID() {
        return graph.id;
    }

    public String getGraphName() {
        return graph.name;
    }

    public boolean isGraphDisable() {
        return graph.disable;
    }

    public String getGraphColor() {
        return graph.disable ? " gray" : "";
    }

    public String getDecreaseName() {
        return decrease.name;
    }

    public Integer getDecreaseID() {
        return decrease.id;
    }

    public boolean isDecreaseDisable() {
        return decrease.disable;
    }

    public String getDecreaseColor() {
        return decrease.disable ? " gray" : "";
    }

    public Double gettMin() {
        return t.min;
    }

    public String gettMinColor() {
        return t.minDisable ? " gray" : "";
    }

    public boolean istMinDisable() {
        return t.minDisable;
    }

    public Double gettMax() {
        return t.max;
    }

    public String gettMaxColor() {
        return t.maxDisable ? " gray" : "";
    }

    public boolean istMaxDisable() {
        return t.maxDisable;
    }

    public boolean istPersentDisable() {
        return istMaxDisable() && istMinDisable();
    }

    public Double getaMin() {
        return a.min;
    }

    public String getaMinColor() {
        return a.minDisable ? " gray" : "";
    }

    public boolean isaMinDisable() {
        return a.minDisable;
    }

    public Double getaMax() {
        return a.max;
    }

    public String getaMaxColor() {
        return a.maxDisable ? " gray" : "";
    }

    public boolean isaMaxDisable() {
        return a.maxDisable;
    }

    public boolean isaPersentDisable() {
        return isaMaxDisable() && isaMinDisable();
    }

    public boolean istPercent() {
        return t.percent;
    }

    public boolean isaPercent() {
        return a.percent;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void settMin(Double tMin) {
        t.min = tMin;
    }

    public void settMax(Double tMax) {
        t.max = tMax;
    }

    public void settPercent(boolean tPercent) {
        t.percent = tPercent;
    }

    public void setaMin(Double aMin) {
        a.min = aMin;
    }

    public void setaMax(Double aMax) {
        a.max = aMax;
    }

    public void setaPercent(boolean aPercent) {
        if (aPercent) {
            setAbsolute(true);
        }
        a.percent = aPercent;
    }

    public void setDecreaseName(String name) {
        decrease.name = name;
    }

    public void setDecreaseID(Integer id) {
        decrease.id = id;
    }

    public void setGraphName(String name) {
        graph.name = name;
    }

    public void setGraphID(Integer id) {
        graph.id = id;
    }

    public String getColor() {
        return color ? " red" : "";
    }

    private class GraphDecrease {

        private Integer id;
        private String name;
        private boolean disable = true;

        private GraphDecrease() {
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", GraphDecrease.class.getSimpleName() + "[", "]")
                    .add("id=" + id)
                    .add("name='" + name + "'")
                    .add("disable=" + disable)
                    .toString();
        }
    }

    private class Borders {

        private Double min;
        private Double max;

        private boolean minDisable;
        private boolean maxDisable;

        private boolean percent;

        private Borders() {
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Borders.class.getSimpleName() + "[", "]")
                    .add("min=" + min)
                    .add("max=" + max)
                    .add("minDisable=" + minDisable)
                    .add("maxDisable=" + maxDisable)
                    .add("percent=" + percent)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AnalogData.class.getSimpleName() + "[", "]")
                .add("graph=" + graph)
                .add("decrease=" + decrease)
                .add("t=" + t)
                .add("a=" + a)
                .add("absolute=" + absolute)
                .add("color=" + color)
                .toString();
    }
}
