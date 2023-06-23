package ru.tecon.admTools.systemParams.model;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс описывающий единицы измерений свойств структуры
 * @author Maksim Shchelkonogov
 */
public class Measure implements Serializable {

    private Integer id = 0;
    private String name;
    private String shortName;
    private String shortNameWithSupTag;
    private String shortNameWithSupMnemonic;

    public Measure() {
    }

    public Measure(String name) {
        this();
        this.name = name;
    }

    public Measure(Integer id, String name, String shortName) {
        this();
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        createShortNameWithSupTag();
        createShortNameWithSupMnemonic();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * Создание сокращенного название для html с
     * добавлением тега {@literal "<sup></sup>"} для написания надстрочных степеней
     */
    private void createShortNameWithSupTag() {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = Pattern.compile("(\\d+)").matcher(shortName);
        while (matcher.find()) {
            output.append(shortName, lastIndex, matcher.start())
                    .append("<sup>")
                    .append(matcher.group(1))
                    .append("</sup>");

            lastIndex = matcher.end();
        }
        if (lastIndex < shortName.length()) {
            output.append(shortName, lastIndex, shortName.length());
        }
        shortNameWithSupTag = output.toString();
    }

    /**
     * Создание сокращенного название для html с
     * добавлением тега {@literal "&sup"} для написания надстрочных степеней
     */
    private void createShortNameWithSupMnemonic() {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = Pattern.compile("(\\d)").matcher(shortName);
        while (matcher.find()) {
            output.append(shortName, lastIndex, matcher.start())
                    .append("&sup")
                    .append(matcher.group(1));

            lastIndex = matcher.end();
        }
        if (lastIndex < shortName.length()) {
            output.append(shortName, lastIndex, shortName.length());
        }
        shortNameWithSupMnemonic = output.toString();
    }

    /**
     * @return Получение сокращенного название для html с
     * добавлением тега {@literal "<sup></sup>"} для написания надстрочных степеней
     */
    public String getShortNameWithSupTag() {
       return shortNameWithSupTag;
    }

    /**
     * @return Получение сокращенного название для html с
     * добавлением тега {@literal "&sup"} для написания надстрочных степеней
     */
    public String getShortNameWithSupMnemonic() {
        return shortNameWithSupMnemonic;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        createShortNameWithSupTag();
        createShortNameWithSupMnemonic();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Measure.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("shortName='" + shortName + "'")
                .toString();
    }
}
