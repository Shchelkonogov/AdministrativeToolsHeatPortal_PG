package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.GenModelMB;
import ru.tecon.admTools.systemParams.model.temperature.Temperature;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для выбора температурного графика на форме Обобщенная модель
 * @author Aleksey Sergeev
 */
@FacesConverter("genModelIsGraphConverter")

public class GenModelIsGraphConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{genModelMB}", GenModelMB.class);

        GenModelMB defaultValues = (GenModelMB) vex.getValue(context.getELContext());


            return defaultValues.getIsGraphList().stream()
                    .filter(isGraph -> isGraph.getId() == Integer.parseInt(value))
                    .findFirst()
                    .orElse(null);
        }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((Temperature) value).getId());
    }
}
