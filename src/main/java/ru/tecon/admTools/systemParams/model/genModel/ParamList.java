package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий параметры для таблицы вычислимых
 * @author Aleksey Sergeev
 */
public class ParamList implements Serializable {
    private Long id;
    private String par_memo;
    private String par_name;

    public ParamList() {
    }

    public ParamList(Long id, String par_memo, String par_name) {
        this.id = id;
        this.par_memo = par_memo;
        this.par_name = par_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPar_memo() {
        return par_memo;
    }

    public void setPar_memo(String par_memo) {
        this.par_memo = par_memo;
    }

    public String getPar_name() {
        return par_name;
    }

    public void setPar_name(String par_name) {
        this.par_name = par_name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamList.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("par_memo='" + par_memo + "'")
                .add("par_name='" + par_name + "'")
                .toString();
    }
}
