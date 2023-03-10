package ru.tecon.admTools.systemParams.model.mainParam;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий техпроцесс
 * @author Aleksey Sergeev
 */
public class TechProc implements Serializable {
    private int objid;
    private int id;
    private String name;
    private String characteristics;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TechProc(String name) {
        this.name = name;
    }

    public TechProc(int objid, int id, String name, String characteristics) {
        this.objid = objid;
        this.id = id;
        this.name = name;
        this.characteristics = characteristics;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TechProc.class.getSimpleName() + "[", "]")
                .add("objid=" + objid)
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("characteristics='" + characteristics + "'")
                .toString();
    }
}
