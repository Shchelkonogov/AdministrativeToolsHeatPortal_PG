package ru.tecon.admTools.mobile.model;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 22.04.2025
 */
public class UserObject {

    private final int id;
    private final String name;

    public UserObject(int id, String name) {
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
        return new StringJoiner(", ", UserObject.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
