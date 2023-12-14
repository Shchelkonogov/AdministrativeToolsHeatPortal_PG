package ru.tecon.admTools.specificModel.model;

import java.util.StringJoiner;

public class GraphDecreaseDescription {

    private final String x;
    private final String y;

    public GraphDecreaseDescription(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GraphDecreaseDescription.class.getSimpleName() + "[", "]")
                .add("x='" + x + "'")
                .add("y='" + y + "'")
                .toString();
    }
}
