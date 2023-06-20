package ru.tecon.admTools.systemParams.model;

import java.util.StringJoiner;

/**
 * Класс для хранения информации по цветам параметров
 * @author Maksim Shchelkonogov
 */
public class ParametersColor {

    private final int id;
    private final String name;
    private String oldColor;
    private String newColor;

    public ParametersColor(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.oldColor = color;
        this.newColor = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return newColor;
    }

    public void setColor(String color) {
        this.newColor = color;
    }

    /**
     * Подтверждение изменения цвета
     */
    public void updateColor() {
        oldColor = newColor;
    }

    /**
     * Отмена изменения цвета
     */
    public void revert() {
        newColor = oldColor;
    }

    /**
     * Проверка изменили ли цвет
     * @return true цвет новый
     */
    public boolean isChanged() {
        return !oldColor.equals(newColor);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParametersColor.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("oldColor='" + oldColor + "'")
                .add("newColor='" + newColor + "'")
                .toString();
    }
}
