package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.List;
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
    private boolean disableStatAgr;
    private List <StatAgrList> rowList;


    public CalcAgrVars(Long calcParId, Long calcStatAgrId, String variable, ParamList paramList, StatAgrList statAgrList, boolean disableStatAgr, List<StatAgrList> rowList) {
        this.calcParId = calcParId;
        this.calcStatAgrId = calcStatAgrId;
        this.variable = variable;
        this.paramList = paramList;
        this.statAgrList = statAgrList;
        this.disableStatAgr = disableStatAgr;
        this.rowList = rowList;
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

    public boolean isDisableStatAgr() {
        return disableStatAgr;
    }

    public void setDisableStatAgr(boolean disableStatAgr) {
        this.disableStatAgr = disableStatAgr;
    }

    public List<StatAgrList> getRowList() {
        return rowList;
    }

    public void setRowList(List<StatAgrList> rowList) {
        this.rowList = rowList;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CalcAgrVars.class.getSimpleName() + "[", "]")
                .add("calcParId=" + calcParId)
                .add("calcStatAgrId=" + calcStatAgrId)
                .add("variable='" + variable + "'")
                .add("paramList=" + paramList)
                .add("statAgrList=" + statAgrList)
                .add("disableStatAgr=" + disableStatAgr)
                .add("rowList=" + rowList)
                .toString();
    }
}
