package ru.tecon.admTools.utils;

import org.primefaces.PrimeFaces;

/**
 * @author Maksim Shchelkonogov
 * 04.12.2023
 */
public class TeconMessage {

    private final Severity severity;
    private final String header;
    private final String message;

    public static final Severity SEVERITY_INFO = new Severity("info");
    public static final Severity SEVERITY_WARN = new Severity("warn");
    public static final Severity SEVERITY_ERROR = new Severity("error");
    public static final Severity SEVERITY_SUCCESS = new Severity("success");

    public TeconMessage(Severity severity, String header, String message) {
        this.severity = severity;
        this.header = header;
        this.message = message;
    }

    /**
     * Отправка данного сообщения
     */
    public void send() {
        PrimeFaces.current().executeScript("window.parent.postMessage({severity: '" + severity.severityName + "', " +
                "summary: '" + header + "', detail: '" + message + "'}, '*');");
    }

    private static class Severity {

        private final String severityName;

        private Severity(String severityName) {
            this.severityName = severityName;
        }
    }
}
