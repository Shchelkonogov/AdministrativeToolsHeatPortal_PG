package ru.tecon.admTools.systemParams.model.struct;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру
 * @author Maksim Shchelkonogov
 */
public class StructType implements Serializable, Comparable<StructType> {

    private int id;
    private String name;
    private String typeChar;
    private Long parentID;

    public StructType() {
    }

    public StructType(int id, String name, String typeChar) {
        this.id = id;
        this.name = name;
        this.typeChar = typeChar;
    }

    public StructType(int id, String name, String typeChar, Long parentID) {
        this.id = id;
        this.name = name;
        this.typeChar = typeChar;
        this.parentID = parentID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTypeChar() {
        return typeChar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeChar(String typeChar) {
        this.typeChar = typeChar;
    }

    public Long getParentID() {
        return parentID;
    }

    public void setParentID(Long parentID) {
        this.parentID = parentID;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StructType.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("typeChar='" + typeChar + "'")
                .add("parentID=" + parentID)
                .toString();
    }

    @Override
    public int compareTo(StructType o) {
        return getName().compareTo(o.getName());
    }
}
