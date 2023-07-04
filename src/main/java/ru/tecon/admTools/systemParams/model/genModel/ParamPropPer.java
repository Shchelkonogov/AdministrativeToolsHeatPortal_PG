package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру перечислений перечислимого стат. агрегата параметра обобщенной модели
 *
 * @author Aleksey Sergeev
 */
public class ParamPropPer implements Serializable {

    private long parId;
    private long paramTypeId;
    private long statAgrId;
    private long enumCode;
    private Condition propCond;

    public ParamPropPer() {
    }

    public ParamPropPer(long parId, long paramTypeId, long statAgrId, long enumCode, Condition propCond) {
        this();
        this.parId = parId;
        this.paramTypeId = paramTypeId;
        this.statAgrId = statAgrId;
        this.enumCode = enumCode;
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

    public Condition getPropCond() {
        return propCond;
    }

    public void setPropCond(Condition propCond) {
        this.propCond = propCond;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamPropPer.class.getSimpleName() + "[", "]")
                .add("parId=" + parId)
                .add("paramTypeId=" + paramTypeId)
                .add("statAgrId=" + statAgrId)
                .add("enumCode=" + enumCode)
                .add("propCond=" + propCond)
                .toString();
    }
}
