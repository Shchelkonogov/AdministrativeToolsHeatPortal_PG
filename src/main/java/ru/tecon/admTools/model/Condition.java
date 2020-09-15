package ru.tecon.admTools.model;

import ru.tecon.admTools.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.StringJoiner;

public class Condition implements Serializable {

    private int id;
    private String name;

    public Condition(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCondSer() throws IOException {
        return Utils.toString(this);
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

    @Override
    public String toString() {
        return new StringJoiner(", ", Condition.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
