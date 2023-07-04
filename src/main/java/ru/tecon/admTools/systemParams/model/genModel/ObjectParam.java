package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 03.07.2023
 */
public class ObjectParam implements Serializable {

    private int id;
    private String memo;
    private String name;

    public ObjectParam() {
    }

    public ObjectParam(int id, String memo, String name) {
        this();
        this.id = id;
        this.memo = memo;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getMemo() {
        return memo;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjectParam.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("memo='" + memo + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
