package ru.tecon.admTools.systemParams.model.struct;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий единицы измерений свойств структуры
 * @author Maksim Shchelkonogov
 */
public class Measure implements Serializable {

    private Integer id;
    private String name;
    private String shortName;

    public Measure() {
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

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
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
