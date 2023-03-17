package ru.tecon.admTools.systemParams.model.mainParam;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий тип объекта
 * @author Aleksey Sergeev
 */

public class ObjType implements Serializable {
    private int id;
    private String name;
    private String characteristics;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjType(int id, String name, String characteristics) {
        this.id = id;
        this.name = name;
        this.characteristics = characteristics;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjType.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("characteristics='" + characteristics + "'")
                .toString();
    }
}
