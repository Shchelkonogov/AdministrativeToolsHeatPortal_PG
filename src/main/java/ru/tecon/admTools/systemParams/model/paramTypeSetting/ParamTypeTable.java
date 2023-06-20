package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import ru.tecon.admTools.systemParams.model.Measure;

import java.io.Serializable;
import java.util.*;

/**
 * Модель данных для таблицы типы параметров в форме Настройка типа параметра
 * @author Maksim Shchelkonogov
 * 16.02.2023
 */
public class ParamTypeTable implements Serializable {

    private UUID rowIndex;
    private ParamType paramType;
    private StatisticalAggregate aggregate;
    private Measure measure;
    private String category;
    private boolean mainAggregate;

    public ParamTypeTable() {
        rowIndex = UUID.randomUUID();
    }

    public ParamTypeTable(StatisticalAggregate aggregate) {
        this();
        this.aggregate = aggregate;
    }

    public ParamTypeTable(ParamType paramType, StatisticalAggregate aggregate, Measure measure, String category, boolean mainAggregate) {
        this();
        this.paramType = paramType;
        this.aggregate = aggregate;
        this.measure = measure;
        this.category = category;
        this.mainAggregate = mainAggregate;
    }

    public boolean check() {
        return !((paramType == null) && (aggregate != null) && (measure != null) && (category.equals("A") || category.equals("P")));
    }

    public UUID getRowIndex() {
        return rowIndex;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public StatisticalAggregate getAggregate() {
        return aggregate;
    }

    public Measure getMeasure() {
        return measure;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isMainAggregate() {
        return mainAggregate;
    }

    public void setMainAggregate(boolean mainAggregate) {
        this.mainAggregate = mainAggregate;
    }

    public void setAggregate(StatisticalAggregate aggregate) {
        this.aggregate = aggregate;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamTypeTable.class.getSimpleName() + "[", "]")
                .add("rowIndex=" + rowIndex)
                .add("paramType=" + paramType)
                .add("aggregate=" + aggregate)
                .add("measure=" + measure)
                .add("category='" + category + "'")
                .add("mainAggregate=" + mainAggregate)
                .toString();
    }
}
