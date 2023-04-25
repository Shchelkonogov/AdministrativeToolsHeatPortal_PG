package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий стат. агрегат для таблицы вычислимых
 * @author Aleksey Sergeev
 */
public class StatAgrList implements Serializable {
    private Long stat_agr_id;
    private String stat_agr_code;
    private String stat_agr_name;

    public StatAgrList() {
    }

    public StatAgrList(Long stat_agr_id, String stat_agr_code) {
        this.stat_agr_id = stat_agr_id;
        this.stat_agr_code = stat_agr_code;
    }

    public Long getStat_agr_id() {
        return stat_agr_id;
    }

    public void setStat_agr_id(Long stat_agr_id) {
        this.stat_agr_id = stat_agr_id;
    }

    public String getStat_agr_code() {
        return stat_agr_code;
    }

    public void setStat_agr_code(String stat_agr_code) {
        this.stat_agr_code = stat_agr_code;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatAgrList.class.getSimpleName() + "[", "]")
                .add("stat_agr_id=" + stat_agr_id)
                .add("stat_agr_code='" + stat_agr_code + "'")
                .add("stat_agr_name='" + stat_agr_name + "'")
                .toString();
    }
}
