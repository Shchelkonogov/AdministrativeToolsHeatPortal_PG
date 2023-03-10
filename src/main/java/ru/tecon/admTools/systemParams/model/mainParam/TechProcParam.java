package ru.tecon.admTools.systemParams.model.mainParam;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий параметры техпроцесса
 * @author Aleksey Sergeev
 */
public class TechProcParam implements Serializable {

    private int techprid;
    private int partypeid;
    private int id;
    private String memo;
    private String name;

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TechProcParam(int techprid, int partypeid, int id, String memo, String name) {
        this.techprid = techprid;
        this.partypeid = partypeid;
        this.id = id;
        this.memo = memo;
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TechProcParam.class.getSimpleName() + "[", "]")
                .add("techprid=" + techprid)
                .add("partypeid=" + partypeid)
                .add("id=" + id)
                .add("memo='" + memo + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
