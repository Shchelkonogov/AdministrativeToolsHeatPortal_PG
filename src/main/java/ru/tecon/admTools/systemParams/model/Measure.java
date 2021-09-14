package ru.tecon.admTools.systemParams.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий единицы измерений свойств структуры
 * @author Maksim Shchelkonogov
 */
public class Measure implements Serializable {

    private Integer id = 0;
    private String name;
    private String shortName;

    public Measure() {
    }

    public Measure(String name) {
        this();
        this.name = name;
    }

    public Measure(Integer id, String name, String shortName) {
        this();
        this.id = id;
        this.name = name;
        this.shortName = shortName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Measure.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("shortName='" + shortName + "'")
                .toString();
    }
}
