package ru.tecon.admTools.systemParams.model.seasonChange;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

/**
 * Класс описывающий структуру таблицы для формы переключение сезона
 * @author Aleksey Sergeev
 */
public class SeasonChangeTable implements Serializable {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private String season;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String name;

    public SeasonChangeTable(String season, LocalDateTime start_time, LocalDateTime end_time, String name) {
    this.season = season;
    this.start_time = start_time;
    this.end_time = end_time;
    this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStart_timeAsString(){
        return start_time.format(dtf);
    }

    public String getEnd_timeAsString(){
        if (end_time==null){
            return "Не окончено";
        }
        return end_time.format(dtf);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SeasonChangeTable.class.getSimpleName() + "[", "]")
                .add("season='" + season + "'")
                .add("start_time=" + start_time)
                .add("end_time=" + end_time)
                .add("name='" + name + "'")
                .toString();
    }
}
