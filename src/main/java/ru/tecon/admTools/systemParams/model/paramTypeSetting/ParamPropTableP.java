package ru.tecon.admTools.systemParams.model.paramTypeSetting;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Структура данных для свойств с категорией P перечислимые
 * @author Maksim Shchelkonogov
 * 22.02.2023
 */
public class ParamPropTableP {

    private UUID rowIndex;
    private int code;
    private String prop;
    private Condition condition;

    public ParamPropTableP() {
        rowIndex = UUID.randomUUID();
    }

    public ParamPropTableP(int code, String prop, Condition condition) {
        this();
        this.code = code;
        this.prop = prop;
        this.condition = condition;
    }

    public int getCode() {
        return code;
    }

    public String getProp() {
        return prop;
    }

    public Condition getCondition() {
        return condition;
    }

    public UUID getRowIndex() {
        return rowIndex;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamPropTableP.class.getSimpleName() + "[", "]")
                .add("rowIndex=" + rowIndex)
                .add("code=" + code)
                .add("prop='" + prop + "'")
                .add("condition=" + condition)
                .toString();
    }
}
