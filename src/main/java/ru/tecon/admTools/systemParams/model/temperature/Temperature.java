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
    private Integer min;
    private Integer max;
    private List<TemperatureProp> temperatureProps = new ArrayList<>();

    public Temperature() {
    }

    public Temperature(int id) {
        this.id = id;
    }

    public Temperature(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Temperature(int id, String name, String description, Integer min, Integer max) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.min = min;
        this.max = max;
    }

    public int getId() {
        return id;
    }
    public Integer getIDInteger(){
        if (id == 0) {
            return null;
        }
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

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
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
                .add("min='" + min + "'")
                .add("max='" + max + "'")
                .toString();
    }
}
