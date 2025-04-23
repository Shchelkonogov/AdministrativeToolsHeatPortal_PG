package ru.tecon.admTools.mobile.model;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 23.04.2025
 */
public class AsyncData {

    private final String name;
    private final String dateTime;
    private final String value;
    private final String state;
    private final String color;

    public AsyncData(String name, String dateTime, String value, String state, String color) {
        this.name = name;
        this.dateTime = dateTime;
        this.value = value;
        this.state = state;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getValue() {
        return value;
    }

    public String getState() {
        return state;
    }

    public String getColor() {
        return color;
    }

    public String getColorCss() {
        if ((color == null) || !color.equals("1")) {
            return "";
        } else {
            return "background-color: yellow;";
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AsyncData.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("dateTime=" + dateTime)
                .add("value='" + value + "'")
                .add("state='" + state + "'")
                .add("color='" + color + "'")
                .toString();
    }
}
