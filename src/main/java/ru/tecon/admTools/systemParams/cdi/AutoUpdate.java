package ru.tecon.admTools.systemParams.cdi;

/**
 * Интерфейс для возможности обновления компоненты.
 *
 * @author Maksim Shchelkonogov
 * 19.05.2023
 */
public interface AutoUpdate {

    /**
     * Обновление в начальное состояние
     */
    void update();
}
