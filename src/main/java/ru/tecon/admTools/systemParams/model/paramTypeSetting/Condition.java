package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.02.2023
 */
public class Condition implements Serializable {

    private int id;
    private String name;

    public Condition() {
    }

    public Condition(int id, String name) {
        this();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return id == condition.id && Objects.equals(name, condition.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Condition.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
