package ru.tecon.admTools.systemParams.model.temperature;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс описывающий свйоства типы для форм суточные снижения и температурные графики
 * @author Maksim Shchelkonogov
 */
public class TemperatureProp implements Serializable {

    private static final AtomicInteger count = new AtomicInteger(0);

    private int id;
    private int tnv;
    private int value;
    private boolean changed = false;

    public TemperatureProp() {
        id = count.addAndGet(1);
    }

    public TemperatureProp(int tnv, int value) {
        this();
        this.tnv = tnv;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getTnv() {
        return tnv;
    }

    public int getValue() {
        return value;
    }

    public void setTnv(int tnv) {
        this.tnv = tnv;
        changed = true;
    }

    public void setValue(int value) {
        this.value = value;
        changed = true;
    }

    public boolean check() {
        return !changed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TemperatureProp.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("tnv=" + tnv)
                .add("value=" + value)
                .toString();
    }
}
