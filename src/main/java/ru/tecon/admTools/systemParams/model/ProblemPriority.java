package ru.tecon.admTools.systemParams.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Класс описывающий данные для приоритет проблем
 * @author Maksim Shchelkonogov
 */
public class ProblemPriority implements Serializable {

    private int id;
    private String name;
    private int oldPriority;
    private int newPriority;

    public ProblemPriority(int id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.newPriority = priority;
        this.oldPriority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return newPriority;
    }

    public void setPriority(int priority) {
        this.newPriority = priority;
    }

    /**
     * Подтверждение изменения приоритета
     */
    public void updatePriority() {
        oldPriority = newPriority;
    }

    /**
     * Отмена изменения приоритета
     */
    public void revert() {
        newPriority = oldPriority;
    }

    /**
     * Проверка изменили ли приоритет
     * @return true приоритет новый
     */
    public boolean isChanged() {
        return oldPriority != newPriority;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProblemPriority.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("oldPriority=" + oldPriority)
                .add("newPriority=" + newPriority)
                .toString();
    }
}
