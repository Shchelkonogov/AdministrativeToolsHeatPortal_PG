package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий возвращаемую расшифровку формулу вычислимого стат. агрегата параметра обобщенной модели
 * @author Aleksey Sergeev
 */
public class CalcAgrVars implements Serializable {
    private Long calcParId;
    private Long calcStatAgrId;
    private String variable;
    private ParamList paramList;
    private StatAgrList statAgrList;
    private boolean statAgrDisable;
    private boolean paramDisable;

    public CalcAgrVars(Long calcParId, Long calcStatAgrId, String variable, ParamList paramList, StatAgrList statAgrList, boolean statAgrDisable, boolean paramDisable) {
        this.calcParId = calcParId;
        this.calcStatAgrId = calcStatAgrId;
        this.variable = variable;
        this.paramList = paramList;
        this.statAgrList = statAgrList;
        this.statAgrDisable = statAgrDisable;
        this.paramDisable = paramDisable;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public ParamList getParamList() {
        return paramList;
    }

    public void setParamList(ParamList paramList) {
        this.paramList = paramList;
    }

    public StatAgrList getStatAgrList() {
        return statAgrList;
    }

    public void setStatAgrList(StatAgrList statAgrList) {
        this.statAgrList = statAgrList;
    }

    public boolean isStatAgrDisable() {
        return statAgrDisable;
    }

    public void setStatAgrDisable(boolean statAgrDisable) {
        this.statAgrDisable = statAgrDisable;
    }

    public boolean isParamDisable() {
        return paramDisable;
    }

    public void setParamDisable(boolean paramDisable) {
        this.paramDisable = paramDisable;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CalcAgrVars.class.getSimpleName() + "[", "]")
                .add("calcParId=" + calcParId)
                .add("calcStatAgrId=" + calcStatAgrId)
                .add("variable='" + variable + "'")
                .add("paramList=" + paramList)
                .add("statAgrList=" + statAgrList)
                .add("statAgrDisable=" + statAgrDisable)
                .add("paramDisable=" + paramDisable)
                .toString();
    }
}
