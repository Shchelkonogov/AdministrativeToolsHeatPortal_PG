package ru.tecon.admTools.mobile.model;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 22.04.2025
 */
public class ObjectData {

    private final String name;
    private List<Value> values;

    public ObjectData(String name) {
        this.name = name;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<Value> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjectData.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("values=" + values)
                .toString();
    }

    public static class Value {
        private final String value;
        private final String color;

        public Value(String value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public String getColor() {
            return color;
        }

        public String getColorCss() {
            if ((color == null) || color.isEmpty()) {
                return "";
            } else {
                return "background-color: #" + color + ";";
            }
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Value.class.getSimpleName() + "[", "]")
                    .add("value='" + value + "'")
                    .add("color='" + color + "'")
                    .toString();
        }
    }
}
