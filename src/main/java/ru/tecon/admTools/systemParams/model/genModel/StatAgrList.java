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
    private String variable;

    public StatAgrList() {
    }

    public StatAgrList(Long statAgrId, String statAgrCode) {
        this.statAgrId = statAgrId;
        this.statAgrCode = statAgrCode;
    }

    public Long getStatAgrId() {
        return statAgrId;
    }

    public String getStatAgrCode() {
        return statAgrCode;
    }

    public void setStatAgrCode(String statAgrCode) {
        this.statAgrCode = statAgrCode;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatAgrList.class.getSimpleName() + "[", "]")
                .add("statAgrId=" + statAgrId)
                .add("statAgrCode='" + statAgrCode + "'")
                .add("variable='" + variable + "'")
                .toString();
    }
}