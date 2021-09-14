package ru.tecon.admTools.systemParams.model;

import ru.tecon.admTools.systemParams.model.struct.PropValType;

import java.util.StringJoiner;

/**
 * Класс описывающий системные свойства
 * @author Maksim Shchelkonogov
 */
public class SysProp {

    private int id;
    private String name;
    private PropValType type;
    private Measure measure;
    private String def;

    public SysProp(String name) {
        this.name = name;
    }

    public SysProp(int id, String name, PropValType type, Measure measure, String def) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.measure = measure;
        this.def = def;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropValType getType() {
        return type;
    }

    public void setType(PropValType type) {
        this.type = type;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SysProp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("type=" + type)
                .add("measure=" + measure)
                .add("def='" + def + "'")
                .toString();
    }
}
