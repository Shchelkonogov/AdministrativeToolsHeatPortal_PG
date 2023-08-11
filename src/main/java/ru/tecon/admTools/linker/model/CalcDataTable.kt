package ru.tecon.admTools.linker.model

/**
 * Данные для таблицы в закладке "Линкованные объекты / Вычислимые параметры"
 *
 * @author Maksim Shchelkonogov
 * 10.08.2023
 */
class CalcDataTable(val name: String, val statAggr: String, val fullName: String, val color: Int) {

    val styleClass: String
        get() = when (color) {
            0 -> "redRow"
            1 -> "greenRow"
            else -> ""
        }
}
