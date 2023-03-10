package ru.tecon.admTools.systemParams.model.mainParam;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру таблицы основных параметров
 * @author Aleksey Sergeev
 */
public class MPTable implements Serializable {
    private int objid;
    private int techprid;
    private int partypeid;
    private int id;
    private String name;
    private String memo;

    public int getObjid() {
        return objid;
    }

    public int getTechprid() {
        return techprid;
    }

    public int getPartypeid() {
        return partypeid;
    }

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public MPTable() {
    }

    public MPTable(int objid, int techprid, int partypeid, int id, String name, String memo) {
        this.objid = objid;
        this.techprid = techprid;
        this.partypeid = partypeid;
        this.id = id;
        this.name = name;
        this.memo = memo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MPTable.class.getSimpleName() + "[", "]")
                .add("objid=" + objid)
                .add("techprid=" + techprid)
                .add("partypeid=" + partypeid)
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("memo='" + memo + "'")
                .toString();
    }
}
