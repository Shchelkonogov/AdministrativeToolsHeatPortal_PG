package ru.tecon.admTools.systemParams.model.plannedOutages;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс для указания границы возможного ввода начала планового отключения в днях от текущей
 * даты для формы Плановые отключения
 *
 * @author Aleksey Sergeev
 */

public class ShutdownRange implements Serializable {

    private int shutdownRange;
    private int newShutdownRange;

    public ShutdownRange() {
    }

    public ShutdownRange(int shutdownRange) {
        this();
        this.shutdownRange = shutdownRange;
        this.newShutdownRange = shutdownRange;
    }

    public int getShutdownRange() {
        return newShutdownRange;
    }

    public void setShutdownRange(int shutdownRange) {
        this.newShutdownRange = shutdownRange;
    }

    public boolean isChanged() {
        return shutdownRange != newShutdownRange;
    }

    /**
     * Подтверждение изменения значения
     */
    public void updateValue() {
        shutdownRange = newShutdownRange;
    }

    /**
     * Отмена изменения значения
     */
    public void revert() {
        newShutdownRange = shutdownRange;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ShutdownRange.class.getSimpleName() + "[", "]")
                .add("shutdownRange=" + shutdownRange)
                .add("newShutdownRange=" + newShutdownRange)
                .toString();
    }
}
