package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 17.02.2023
 */
public class ParamType implements Comparable<ParamType> {

    private int id;
    private String name;
    private boolean create;

    public ParamType(String name) {
        this.name = name;
        create = true;
    }

    public ParamType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCreate() {
        return create;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamType paramType = (ParamType) o;
        return id == paramType.id &&
                Objects.equals(name, paramType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamType.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("create=" + create)
                .toString();
    }

    @Override
    public int compareTo(ParamType o) {
        return getName().compareTo(o.getName());
    }
}
