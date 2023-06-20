package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру свойств аналогового стат. агрегата параметра обобщенной модели
 * @author Aleksey Sergeev
 */
public class ParamProp implements Serializable {

    private long parId;
    private long paramTypeId;
    private long statAgrId;
    private long propId;
    private String propName;
    private String propValDef;
    private Condition greatCond;
    private Condition lessCond;

    public ParamProp(long parId, long paramTypeId, long statAgrId, long propId, String propName, String propValDef, Condition greatCond, Condition lessCond) {
        this.parId = parId;
        this.paramTypeId = paramTypeId;
        this.statAgrId = statAgrId;
        this.propId = propId;
        this.propName = propName;
        this.propValDef = propValDef;
        this.greatCond = greatCond;
        this.lessCond = lessCond;
    }

    public long getParId() {
        return parId;
    }

    public void setParId(long parId) {
        this.parId = parId;
    }

    public long getStatAgrId() {
        return statAgrId;
    }

    public long getPropId() {
        return propId;
    }

    public void setPropId(long propId) {
        this.propId = propId;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropValDef() {
        return propValDef;
    }

    public void setPropValDef(String propValDef) {
        this.propValDef = propValDef;
    }

    public Condition getGreatCond() {
        return greatCond;
    }

    public void setGreatCond(Condition greatCond) {
        this.greatCond = greatCond;
    }

    public Condition getLessCond() {
        return lessCond;
    }

    public void setLessCond(Condition lessCond) {
        this.lessCond = lessCond;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamProp.class.getSimpleName() + "[", "]")
                .add("parId=" + parId)
                .add("paramTypeId=" + paramTypeId)
                .add("statAgrId=" + statAgrId)
                .add("propId=" + propId)
                .add("propName='" + propName + "'")
                .add("propValDef='" + propValDef + "'")
                .add("greatCond=" + greatCond)
                .add("lessCond=" + lessCond)
                .toString();
    }
}
