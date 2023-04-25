package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;

public class CalcAgrFormula implements Serializable{
    private String variable;

    private Long par_id;
    private Long stat_agr_id;

    public CalcAgrFormula(String variable, Long par_id, Long stat_agr_id) {
        this.variable = variable;
        this.par_id = par_id;
        this.stat_agr_id = stat_agr_id;
    }

    @Override
    public String toString() {
        return "(" + variable+", " + par_id + ", " + stat_agr_id + ')';
    }
}
