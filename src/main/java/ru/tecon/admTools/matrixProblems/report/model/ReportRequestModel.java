package ru.tecon.admTools.matrixProblems.report.model;

import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.StringJoiner;

/**
 * Модель данных, для формирования отчета по матрицы проблем, получаемый из json
 */
public class ReportRequestModel implements Serializable {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private int structID;
    private int filterID;
    private String filterValue;
    private String date;
    @JsonbTransient
    private transient LocalDate firstDateAtMonth;
    @JsonbTransient
    private transient String firstDateAtMonthValue;

    public ReportRequestModel() {
    }

    /**
     * Контструктор
     * @param structID id структуры
     * @param filterID id фильтра
     * @param filterValue значение фильтра
     * @param date дата в формате dd.mm.yyyy
     */
    public ReportRequestModel(int structID, int filterID, String filterValue, String date) throws DateTimeParseException {
        this.structID = structID;
        this.filterID = filterID;
        this.filterValue = filterValue;
        setDate(date);
    }

    public int getStructID() {
        return structID;
    }

    public void setStructID(int structID) {
        this.structID = structID;
    }

    public int getFilterID() {
        return filterID;
    }

    public void setFilterID(int filterID) {
        this.filterID = filterID;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) throws DateTimeParseException {
        this.date = date;
        firstDateAtMonth = LocalDate.parse(date, FORMATTER).withDayOfMonth(1);
        firstDateAtMonthValue = firstDateAtMonth.format(FORMATTER);
    }

    public LocalDate getFirstDateAtMonth() {
        return firstDateAtMonth;
    }

    public String getFirstDateAtMonthValue() {
        return firstDateAtMonthValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReportRequestModel.class.getSimpleName() + "[", "]")
                .add("structID=" + structID)
                .add("filterID=" + filterID)
                .add("filterValue='" + filterValue + "'")
                .add("date='" + date + "'")
                .add("firstDateAtMonth=" + firstDateAtMonth)
                .add("firstDateAtMonthValue='" + firstDateAtMonthValue + "'")
                .toString();
    }
}
