package ru.tecon.admTools.specificModel.model;

import java.util.StringJoiner;

public class ParamHistory {

    private final String date;
    private final String userName;
    private final String description;
    private final String oldValue;
    private final String newValue;

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
