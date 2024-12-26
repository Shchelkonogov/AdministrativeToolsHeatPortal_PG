package ru.tecon.admTools.systemParams;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import java.util.logging.Logger;

/**
 * @author Maksim Shchelkonogov
 * 17.02.2023
 */
public class LoggerProducer {

    @Produces
    public Logger getLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
