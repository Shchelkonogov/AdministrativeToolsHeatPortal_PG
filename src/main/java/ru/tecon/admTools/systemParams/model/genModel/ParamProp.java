package ru.tecon.admTools.systemParams.model.genModel;

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
    private long propCondGreat;
    private String propCondGreatName;
    private long propCondLess;
    private String propCondLessName;

    public ParamProp() {
    }

    public ParamProp(long parId, long paramTypeId, long statAgrId, long propId, String propName, String propValDef, long propCondGreat, String propCondGreatName, long propCondLess, String propCondLessName) {
        this.parId = parId;
        this.paramTypeId = paramTypeId;
        this.statAgrId = statAgrId;
        this.propId = propId;
        this.propName = propName;
        this.propValDef = propValDef;
        this.propCondGreat = propCondGreat;
        this.propCondGreatName = propCondGreatName;
        this.propCondLess = propCondLess;
        this.propCondLessName = propCondLessName;
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

    public String getPropCondGreatName() {
        return propCondGreatName;
    }

    public void setPropCondGreatName(String propCondGreatName) {
        this.propCondGreatName = propCondGreatName;
    }

    public String getPropCondLessName() {
        return propCondLessName;
    }

    public void setPropCondLessName(String propCondLessName) {
        this.propCondLessName = propCondLessName;
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
                .add("propCondGreat=" + propCondGreat)
                .add("propCondGreatName='" + propCondGreatName + "'")
                .add("propCondLess=" + propCondLess)
                .add("propCondLessName='" + propCondLessName + "'")
                .toString();
    }
}
