package ru.tecon.admTools.systemParams;

/**
 * Класс для обработки ошибок о невыполенние функций базы для формы системные параметры
 * @author Maksim Shchelkonogov
 */
public class SystemParamException extends Exception {

    public SystemParamException() {
        super();
    }

    public SystemParamException(String message) {
        super(message);
    }
}
