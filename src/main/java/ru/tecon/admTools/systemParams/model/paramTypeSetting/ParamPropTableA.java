package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Структура данных для свойств с категорией A аналоговые
 * @author Maksim Shchelkonogov
 * 21.02.2023
 */
public class ParamPropTableA implements Comparable<ParamPropTableA> {

    private final UUID rowIndex;
    private Properties prop;
    private Condition lessCond;
    private Condition greatCond;

    public ParamPropTableA() {
        rowIndex = UUID.randomUUID();
    }

    public ParamPropTableA(Properties prop, Condition lessCond, Condition greatCond) {
        this();
        this.prop = prop;
        this.lessCond = lessCond;
        this.greatCond = greatCond;
    }

    public Properties getProp() {
        return prop;
    }

    public Condition getLessCond() {
        return lessCond;
    }

    public Condition getGreatCond() {
        return greatCond;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public void setLessCond(Condition lessCond) {
        this.lessCond = lessCond;
    }

    public void setGreatCond(Condition greatCond) {
        this.greatCond = greatCond;
    }

    public UUID getRowIndex() {
        return rowIndex;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamPropTableA.class.getSimpleName() + "[", "]")
                .add("rowIndex=" + rowIndex)
                .add("prop=" + prop)
                .add("lessCond=" + lessCond)
                .add("greatCond=" + greatCond)
                .toString();
    }

    @Override
    public int compareTo(@NotNull ParamPropTableA o) {
        return prop.compareTo(o.prop);
    }
}
