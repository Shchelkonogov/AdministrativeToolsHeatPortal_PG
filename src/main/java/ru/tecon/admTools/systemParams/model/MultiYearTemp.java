package ru.tecon.admTools.systemParams.model;

import java.util.StringJoiner;

/**
 * Класс описывающий даные для фомы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
public class MultiYearTemp {

    private int id;
    private String name;
    private double value;

    public MultiYearTemp(int id, String name, double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MultiYearTemp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("value=" + value)
                .toString();
    }
}
