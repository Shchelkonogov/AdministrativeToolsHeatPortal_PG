package ru.tecon.admTools.systemParams.model.genModel;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру перечислений перечислимого стат. агрегата параметра обобщенной модели
 * @author Aleksey Sergeev
 */
public class ParamPropPer implements Serializable {
    private long parId;
    private long paramTypeId;
    private long statAgrId;
    private long enumCode;
    private String propVal;
    private long propCond;

    public ParamPropPer() {
    }

    public ParamPropPer(long parId, long paramTypeId, long statAgrId, long enumCode, String propVal, long propCond) {
        this.parId = parId;
        this.paramTypeId = paramTypeId;
        this.statAgrId = statAgrId;
        this.enumCode = enumCode;
        this.propVal = propVal;
        this.propCond = propCond;
    }

    public long getParId() {
        return parId;
    }

    public long getStatAgrId() {
        return statAgrId;
    }

    public long getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(long enumCode) {
        this.enumCode = enumCode;
    }

    public String getPropVal() {
        return propVal;
    }

    public void setPropVal(String propVal) {
        this.propVal = propVal;
    }

    public long getPropCond() {
        return propCond;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", ParamPropPer.class.getSimpleName() + "[", "]")
                .add("parId=" + parId)
                .add("paramTypeId=" + paramTypeId)
                .add("statAgrId=" + statAgrId)
                .add("enumCode=" + enumCode)
                .add("propVal='" + propVal + "'")
                .add("propCond=" + propCond)
                .toString();
    }
}
