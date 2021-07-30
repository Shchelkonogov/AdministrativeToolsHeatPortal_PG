package ru.tecon.admTools.systemParams.model.struct;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывющий категории ствойст структуры
 * @author Maksim Shchelkonogov
 */
public class PropCat implements Serializable {

    private String id;
    private String name;

    public PropCat() {
    }

    public PropCat(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PropCat.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
