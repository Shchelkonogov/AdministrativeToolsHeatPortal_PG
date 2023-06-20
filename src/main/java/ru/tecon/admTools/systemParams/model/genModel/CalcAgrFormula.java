package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;

public class CalcAgrFormula implements Serializable{
    private String variable;

    private Long parId;
    private Long statAgrId;

    public CalcAgrFormula(String variable, Long parId, Long statAgrId) {
        this.variable = variable;
        this.parId = parId;
        this.statAgrId = statAgrId;
    }

    @Override
    public String toString() {
        return "(" + variable+", " + parId + ", " + statAgrId + ')';
    }
}
