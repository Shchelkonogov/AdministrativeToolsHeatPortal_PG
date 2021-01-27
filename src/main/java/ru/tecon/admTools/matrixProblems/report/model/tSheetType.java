package ru.tecon.admTools.matrixProblems.report.model;

/**
 * Перечисление типов excel страниц для отчета по матрице проблем
 */
public enum tSheetType {

    T3("dt"),
    T4("dto"),
    T7("dt"),
    T13("dto"),
    V7("dgv"),
    V13("dgvo"),
    G3("dgv"),
    G4("dgvo"),
    Qco("dgv"),
    Qgvs("dgv");

    private String ratio;

    tSheetType(String ratio) {
        this.ratio = ratio;
    }

    public String getRatio() {
        return ratio;
    }
}
