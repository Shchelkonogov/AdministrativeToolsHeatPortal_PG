package ru.tecon.admTools.systemParams.model.struct;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий названия справочника свойств структур
 * @author Maksim Shchelkonogov
 */
public class SpHeader implements Serializable {

    private Integer id;
    private String name;

    public SpHeader() {
    }

    public SpHeader(Integer id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpHeader.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
