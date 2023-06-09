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
    private boolean changed;

    public ShutdownRange() {
    }

    public ShutdownRange(int shutdownRange, boolean changed) {
        this.shutdownRange = shutdownRange;
        this.changed = changed;
    }

    public int getShutdownRange() {
        return shutdownRange;
    }

    public void setShutdownRange(int shutdownRange) {
        this.shutdownRange = shutdownRange;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ShutdownRange.class.getSimpleName() + "[", "]")
                .add("shutdownRange=" + shutdownRange)
                .toString();
    }
}
