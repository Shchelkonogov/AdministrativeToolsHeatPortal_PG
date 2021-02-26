package ru.tecon.admTools.dataAnalysis.report.model;

import java.util.StringJoiner;

/**
 * Модель данных для значений отчета
 */
public class CellValueModel implements Comparable<CellValueModel> {

    private int row;
    private int column;
    private String value;
    private String styleName;

    /**
     * Конструктор
     * @param row строка
     * @param column колонка
     * @param value значение
     */
    private CellValueModel(int row, int column, String value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    /**
     * Конструктор
     * @param row строка
     * @param column колонка
     * @param value знаение
     * @param styleName имя стиля, для ячейки
     */
    public CellValueModel(int row, int column, String value, String styleName) {
        this(row, column, value);
        this.styleName = styleName;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    public String getStyleName() {
        return styleName;
    }

    @Override
    public int compareTo(CellValueModel o) {
        return Integer.compare(this.row, o.row);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CellValueModel.class.getSimpleName() + "[", "]")
                .add("row=" + row)
                .add("column=" + column)
                .add("value='" + value + "'")
                .add("styleName='" + styleName + "'")
                .toString();
    }
}
