package ru.tecon.admTools.dataAnalysis.model;

import java.util.StringJoiner;

/**
 * Модель данных для критериев
 */
public class Criterion {

    private Integer id;
    private String name;

    public Criterion(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Criterion.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
