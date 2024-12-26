package ru.tecon.admTools.systemParams.cdi.converter.genModel;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.GenModelMB;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;

/**
 * Конвертер для выбора температурного графика на форме Обобщенная модель
 *
 * @author Aleksey Sergeev
 */
@FacesConverter("genModelGraphOrDecreaseConverter")
public class GenModelIsGraphConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{genModelMB}", GenModelMB.class);

        GenModelMB defaultValues = (GenModelMB) vex.getValue(context.getELContext());


        return defaultValues.getGraphOrDecreaseList().stream()
                .filter(isGraph -> isGraph.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(((Temperature) value).getId());
    }
}
