package ru.tecon.admTools.specificModel.model;

import java.io.Serializable;
import java.util.StringJoiner;

public class GraphDecreaseItemModel implements Serializable {

    private int id;
    private String name;
    private String description;

    public GraphDecreaseItemModel() {
    }

    public GraphDecreaseItemModel(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GraphDecreaseItemModel.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .toString();
    }
}
