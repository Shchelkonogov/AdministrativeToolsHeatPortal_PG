package ru.tecon.admTools.systemParams.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий данные для формы Тнв по многолетним наблюдениям
 * @author Maksim Shchelkonogov
 */
public class MultiYearTemp implements Serializable {

    private int id;
    private String name;
    private double value;
    private double newValue;

    public MultiYearTemp() {
    }

    public MultiYearTemp(int id, String name, double value) {
        this();
        this.id = id;
        this.name = name;
        this.value = value;
        this.newValue = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return newValue;
    }

    public void setValue(double value) {
        this.newValue = value;
    }

    /**
     * Подтверждение изменения температуры
     */
    public void updateTemperature() {
        value = newValue;
    }

    /**
     * Отмена изменения температуры
     */
    public void revert() {
        newValue = value;
    }

    /**
     * Проверка изменили ли температуру
     * @return true приоритет новый
     */
    public boolean isChanged() {
        return value != newValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MultiYearTemp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("value=" + value)
                .add("newValue=" + newValue)
                .toString();
    }
}
