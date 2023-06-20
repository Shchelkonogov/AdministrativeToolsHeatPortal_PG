package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий параметры для таблицы вычислимых
 * @author Aleksey Sergeev
 */
public class ParamList implements Serializable {
    private Long id;
    private String parMemo;
    private String parName;

    public ParamList() {
    }

    public ParamList(Long id, String parMemo, String parName) {
        this.id = id;
        this.parMemo = parMemo;
        this.parName = parName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParMemo() {
        return parMemo;
    }

    public void setParMemo(String parMemo) {
        this.parMemo = parMemo;
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamList.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("parMemo='" + parMemo + "'")
                .add("parName='" + parName + "'")
                .toString();
    }
}
