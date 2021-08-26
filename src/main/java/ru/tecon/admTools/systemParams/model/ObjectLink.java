package ru.tecon.admTools.systemParams.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий свзяь объектов
 * @author Maksim Shchelkonogov
 */
public class ObjectLink implements Serializable {

    private int id;
    private String name;
    private int objectTypeLink1;
    private int objectTypeLink2;

    public ObjectLink(String name) {
        this.name = name;
    }

    public ObjectLink(int id, String name, int objectTypeLink1, int objectTypeLink2) {
        this.id = id;
        this.name = name;
        this.objectTypeLink1 = objectTypeLink1;
        this.objectTypeLink2 = objectTypeLink2;
    }

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

    public int getObjectTypeLink1() {
        return objectTypeLink1;
    }

    public void setObjectTypeLink1(int objectTypeLink1) {
        this.objectTypeLink1 = objectTypeLink1;
    }

    public int getObjectTypeLink2() {
        return objectTypeLink2;
    }

    public void setObjectTypeLink2(int objectTypeLink2) {
        this.objectTypeLink2 = objectTypeLink2;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjectLink.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("objectTypeLink1=" + objectTypeLink1)
                .add("objectTypeLink2=" + objectTypeLink2)
                .toString();
    }
}
