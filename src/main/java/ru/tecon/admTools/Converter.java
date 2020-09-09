package ru.tecon.admTools;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.text.DecimalFormat;

@FacesConverter("testConverter")
public class Converter implements javax.faces.convert.Converter {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        FacesMessage msg = new FacesMessage("Ошибка конвертации URL.",
                "{DoubleValue.message}");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        if (Integer.parseInt(value) == 1) {
            throw new ConverterException(msg);
        }
        return Double.parseDouble(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
//        System.out.println(value + " " + FORMAT.format(value));
        return FORMAT.format(value);
    }
}
