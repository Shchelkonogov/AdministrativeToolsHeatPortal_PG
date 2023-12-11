package ru.tecon.admTools.specificModel.model;

import java.util.StringJoiner;

public class ParamHistory {

    private String date;
    private String userName;
    private String description;
    private String oldValue;
    private String newValue;

    public ParamHistory(String date, String userName, String description, String oldValue, String newValue) {
        this.date = date;
        this.userName = userName;
        this.description = description;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getDescription() {
        return description;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamHistory.class.getSimpleName() + "[", "]")
                .add("date='" + date + "'")
                .add("userName='" + userName + "'")
                .add("description='" + description + "'")
                .add("oldValue='" + oldValue + "'")
                .add("newValue='" + newValue + "'")
                .toString();
    }
}
