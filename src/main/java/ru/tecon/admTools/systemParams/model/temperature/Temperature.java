package ru.tecon.admTools.systemParams.model.temperature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Класс описывающий типы для форм суточные снижения и температурные графики
 * @author Maksim Shchelkonogov
 */
public class Temperature implements Serializable {

    private int id;
    private String name;
    private String description;
    private List<TemperatureProp> temperatureProps = new ArrayList<>();

    public Temperature() {
    }

    public Temperature(int id, String name, String description) {
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

    public List<TemperatureProp> getTemperatureProps() {
        return temperatureProps;
    }

    public void setTemperatureProps(List<TemperatureProp> temperatureProps) {
        this.temperatureProps = temperatureProps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Temperature.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .toString();
    }
}
