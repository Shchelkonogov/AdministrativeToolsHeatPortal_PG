package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.02.2023
 */
public class Condition {

    private final int id;
    private final String name;

    public Condition(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Condition.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
