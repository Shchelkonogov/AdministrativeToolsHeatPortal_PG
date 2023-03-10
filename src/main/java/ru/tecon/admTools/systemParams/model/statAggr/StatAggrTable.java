package ru.tecon.admTools.systemParams.model.statAggr;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру таблицы статистических агрегатов
 * @author Aleksey Sergeev
 */
public class StatAggrTable implements Serializable {
    private long id;
    private String stat_agr_code;
    private String stat_agr_name;
    private String dif_int;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStat_agr_code() {
        return stat_agr_code;
    }

    public void setStat_agr_code(String stat_agr_code) {
        this.stat_agr_code = stat_agr_code;
    }

    public String getStat_agr_name() {
        return stat_agr_name;
    }

    public void setStat_agr_name(String stat_agr_name) {
        this.stat_agr_name = stat_agr_name;
    }

    public String getDif_int() {
        return dif_int;
    }

    public void setDif_int(String dif_int) {
        this.dif_int = dif_int;
    }

    public StatAggrTable() {
    }

    public StatAggrTable(long id, String stat_agr_code, String stat_agr_name, String dif_int) {
        this.id = id;
        this.stat_agr_code = stat_agr_code;
        this.stat_agr_name = stat_agr_name;
        this.dif_int = dif_int;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatAggrTable.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("stat_agr_code='" + stat_agr_code + "'")
                .add("stat_agr_name='" + stat_agr_name + "'")
                .add("dif_int='" + dif_int + "'")
                .toString();
    }
}
