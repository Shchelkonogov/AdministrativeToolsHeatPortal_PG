package ru.tecon.admTools.systemParams.model;

import java.util.StringJoiner;

/**
 * Класс описывающий типы объектов
 * @author Maksim Shchelkonogov
 */
public class ObjectType {

    private int id;
    private String name;
    private String code;

    public ObjectType(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjectType.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("code='" + code + "'")
                .toString();
    }
}
