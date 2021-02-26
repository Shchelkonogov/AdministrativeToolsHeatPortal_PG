package ru.tecon.admTools.dataAnalysis.report.model;

import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Модель данных, для формирования отчета "Анализ достоверности", получаемый из json
 */
public class ReportRequestModel implements Serializable {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String objectID;
    private int filterID;
    private String filterValue;
    private String date;
    private String sessionID;
    private List<Integer> problemID = new ArrayList<>();
    private List<Integer> problemOdpuID = new ArrayList<>();
    @JsonbTransient
    private String user;
    @JsonbTransient
    private transient LocalDate firstDateAtMonth;
    @JsonbTransient
    private transient String firstDateAtMonthValue;

    public ReportRequestModel() {
    }

    /**
     * Контструктор
     * @param objectID id структуры
     * @param filterID id фильтра
     * @param filterValue значение фильтра
     * @param date дата в формате dd.mm.yyyy
     */
    public ReportRequestModel(String objectID, int filterID, String filterValue, String date, String sessionID,
                              List<Integer> problemID, List<Integer> problemOdpuID) throws DateTimeParseException {
        this.objectID = objectID;
        this.filterID = filterID;
        this.filterValue = filterValue;
        this.problemID = problemID;
        this.problemOdpuID = problemOdpuID;
        this.sessionID = sessionID;
        setDate(date);
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
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

    public List<Integer> getProblemID() {
        return problemID;
    }

    public void setProblemID(List<Integer> problemID) {
        this.problemID = problemID;
    }

    public Integer getProblemID(int index) {
        return problemID.get(index);
    }

    public List<Integer> getProblemOdpuID() {
        return problemOdpuID;
    }

    public void setProblemOdpuID(List<Integer> problemOdpuID) {
        this.problemOdpuID = problemOdpuID;
    }

    public Integer getProblemOdpuID(int index) {
        return problemOdpuID.get(index);
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReportRequestModel.class.getSimpleName() + "[", "]")
                .add("objectID='" + objectID + "'")
                .add("filterID=" + filterID)
                .add("filterValue='" + filterValue + "'")
                .add("date='" + date + "'")
                .add("sessionID='" + sessionID + "'")
                .add("problemID=" + problemID)
                .add("problemOdpuID=" + problemOdpuID)
                .add("user='" + user + "'")
                .add("firstDateAtMonth=" + firstDateAtMonth)
                .add("firstDateAtMonthValue='" + firstDateAtMonthValue + "'")
                .toString();
    }
}
