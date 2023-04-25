package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий стат. агрегат для таблицы вычислимых
 * @author Aleksey Sergeev
 */
public class StatAgrList implements Serializable {
    private Long statAgrId;
    private String statAgrCode;
    private String statAgrName;

    public StatAgrList() {
    }

    public StatAgrList(Long statAgrId, String statAgrCode) {
        this.statAgrId = statAgrId;
        this.statAgrCode = statAgrCode;
    }

    public Long getStatAgrId() {
        return statAgrId;
    }

    public void setStatAgrId(Long statAgrId) {
        this.statAgrId = statAgrId;
    }

    public String getStatAgrCode() {
        return statAgrCode;
    }

    public void setStatAgrCode(String statAgrCode) {
        this.statAgrCode = statAgrCode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatAgrList.class.getSimpleName() + "[", "]")
                .add("statAgrId=" + statAgrId)
                .add("statAgrCode='" + statAgrCode + "'")
                .add("statAgrName='" + statAgrName + "'")
                .toString();
    }
}
