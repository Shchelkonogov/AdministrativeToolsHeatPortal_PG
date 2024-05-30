package ru.tecon.admTools.specificModel.model;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class ParamHistory {

    private final int propId;
    private final LocalDateTime date;
    private final String userName;
    private final String description;
    private final String oldValue;
    private final String newValue;

    public ParamHistory(int propId, LocalDateTime date, String userName, String description, String oldValue, String newValue) {
        this.propId = propId;
        this.date = date;
        this.userName = userName;
        this.description = description;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public int getPropId() {
        return propId;
    }

    public LocalDateTime getDate() {
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
                .add("propId=" + propId)
                .add("date=" + date)
                .add("userName='" + userName + "'")
                .add("description='" + description + "'")
                .add("oldValue='" + oldValue + "'")
                .add("newValue='" + newValue + "'")
                .toString();
    }
}
