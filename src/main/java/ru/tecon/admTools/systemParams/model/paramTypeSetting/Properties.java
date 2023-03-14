package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 21.02.2023
 */
public class Properties {

    private int id;
    private String name;

    public Properties(int id, String name) {
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
        return new StringJoiner(", ", Properties.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
