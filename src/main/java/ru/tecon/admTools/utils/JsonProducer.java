package ru.tecon.admTools.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.enterprise.inject.Produces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonProducer {

    private static final String DATE_TIME_FORMAT_WRITE = "dd-MM-yyyy HH:mm:ss";
    private static final String DATE_TIME_FORMAT_READ = "dd-MM-yyyy HH:mm:ss";

    @Produces
    public ObjectMapper getJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_WRITE)));
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_READ)));

        objectMapper.registerModules(timeModule);
        return objectMapper;
    }
}
