package ru.tecon.admTools.systemParams.model.struct;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру
 * @author Maksim Shchelkonogov
 */
public class StructType implements Serializable {

    private int id;
    private String name;
    private String typeChar;
    private Integer parentID;

    public StructType() {
    }

    public StructType(int id, String name, String typeChar) {
        this.id = id;
        this.name = name;
        this.typeChar = typeChar;
    }

    public StructType(int id, String name, String typeChar, Integer parentID) {
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

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
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
}
