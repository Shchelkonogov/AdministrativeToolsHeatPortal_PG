package ru.tecon.admTools.cdi;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.text.DecimalFormat;

/**
 * Конвертер проверяет число на тип {@link Double} и отрезает лишние нули справа
 */
@FacesConverter("doubleConverter")
public class DoubleConverter implements javax.faces.convert.Converter {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            if (value.isEmpty()) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибочные данные", "Граница должна быть числом"));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return FORMAT.format(value);
    }
}
