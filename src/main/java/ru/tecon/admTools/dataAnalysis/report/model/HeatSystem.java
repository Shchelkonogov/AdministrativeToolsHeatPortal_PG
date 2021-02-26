package ru.tecon.admTools.dataAnalysis.report.model;

/**
 * Перечисление для отчета "Анализ достоверности".
 * Список используемых теплосистем
 */
public enum HeatSystem {

    TV("ТВ", "counter_tv"),
    CO("ЦО", "counter_co", true),
    CO2("ЦО2", "counter_co2", true),
    VENT("Вентиляция", "counter_vent", true),
    VENT2("Вентиляция2", "counter_vent2", true),
    GVS("ГВС", "counter_gvs", true),
    GVS2("ГВС2", "counter_gvs2", true);

    private String selectName;
    private String name;
    private boolean odpu;

    HeatSystem(String name, String selectName) {
        this.name = name;
        this.selectName = selectName;
    }

    HeatSystem(String name, String selectName, boolean odpu) {
        this.selectName = selectName;
        this.name = name;
        this.odpu = odpu;
    }

    public String getSelectName() {
        return selectName;
    }

    public String getName() {
        return name;
    }

    public boolean isOdpu() {
        return odpu;
    }
}
