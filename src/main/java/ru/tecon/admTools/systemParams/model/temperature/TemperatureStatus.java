package ru.tecon.admTools.systemParams.model.temperature;

/**
 * @author Maksim Shchelkonogov
 * 27.06.2023
 */
public enum TemperatureStatus {

    /**
     * Температурный график
     */
    GRAPH,

    /**
     * Суточные снижения
     */
    DAILY_REDUCTION,

    /**
     * Оптимальное значение
     */
    OPT_VALUE,

    /**
     * Пустой
     */
    EMPTY
}
