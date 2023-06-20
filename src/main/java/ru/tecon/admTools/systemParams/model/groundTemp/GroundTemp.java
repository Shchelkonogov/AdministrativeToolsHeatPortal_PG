package ru.tecon.admTools.systemParams.model.groundTemp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * Класс описывающий данные по температуре грунта
 * @author Maksim Shchelkonogov
 */
public class GroundTemp implements Serializable {

    private LocalDate date;
    private double value;
    private boolean editable = false;

    public GroundTemp() {
        editable = true;
    }

    public GroundTemp(LocalDate date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getStringDate() {
        if (date == null) {
            return "";
        } else {
            return date.format(DateTimeFormatter.ofPattern("LLLL yyyy").withLocale(new Locale("ru")));
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isEditable() {
        return editable;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GroundTemp.class.getSimpleName() + "[", "]")
                .add("date=" + date)
                .add("value='" + value + "'")
                .add("editable=" + editable)
                .toString();
    }
}
