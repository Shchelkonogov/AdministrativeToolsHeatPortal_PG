package ru.tecon.admTools.model;

import ru.tecon.admTools.Utils;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.util.StringJoiner;

public class ParamConditionDataModel {

    private int enumCode;
    private String propValue;
    private boolean edited = false;
    private Condition condition;

    public ParamConditionDataModel(int enumCode, String propValue, String propValueTech, int paramCondID) {
        this.enumCode = enumCode;
        this.propValue = propValue;
        condition = new Condition(paramCondID, propValueTech);
    }

    public int getEnumCode() {
        return enumCode;
    }

    public String getPropValue() {
        return propValue;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        System.out.println(1);
        this.condition = condition;
    }

    public void setCond(String condition) throws IOException, ClassNotFoundException {
        System.out.println(2);
        System.out.println(condition);
        this.condition = (Condition) Utils.fromString(condition);
//        this.condition = condition;
    }

    public String getCond() {
        return "";
    }

    public void setCondition(String condition) {
        System.out.println(3);
//        this.condition = condition;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamConditionDataModel.class.getSimpleName() + "[", "]")
                .add("enumCode=" + enumCode)
                .add("propValue='" + propValue + "'")
                .add("edited=" + edited)
                .add("condition=" + condition)
                .toString();
    }
}
