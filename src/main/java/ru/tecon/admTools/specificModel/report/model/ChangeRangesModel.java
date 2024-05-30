package ru.tecon.admTools.specificModel.report.model;

import ru.tecon.admTools.specificModel.model.ParamHistory;

import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * Модель данных для отчета "Отчет по изменению тех границ"
 * расширяет класс данных {@link ParamHistory}
 */
public class ChangeRangesModel extends ParamHistory {

    private final String objectName;
    private final String parMemo;
    private final String statAgrName;

    public ChangeRangesModel(String objectName, LocalDateTime date, String parMemo, String statAgrName,
                             String description, String oldValue, String newValue, String userName) {
        super(-1, date, userName, description, oldValue, newValue);
        this.objectName = objectName;
        this.parMemo = parMemo;
        this.statAgrName = statAgrName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getParMemo() {
        return parMemo;
    }

    public String getStatAgrName() {
        return statAgrName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChangeRangesModel.class.getSimpleName() + "[", "]")
                .add("objectName='" + objectName + "'")
                .add("parMemo='" + parMemo + "'")
                .add("statAgrName='" + statAgrName + "'")
                .toString();
    }
}
