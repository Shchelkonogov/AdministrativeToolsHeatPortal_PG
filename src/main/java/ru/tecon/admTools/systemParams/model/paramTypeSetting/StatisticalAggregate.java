package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 17.02.2023
 */
public class StatisticalAggregate {

    private int id;
    private String name;
    private String code;

    public StatisticalAggregate(String name) {
        this.name = name;
    }

    public StatisticalAggregate(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatisticalAggregate.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("code='" + code + "'")
                .toString();
    }
}
