package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.DefaultValuesSessionMB;
import ru.tecon.admTools.systemParams.model.ObjectType;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для выбора типа объекта
 * @author Maksim Shchelkonogov
 */
@FacesConverter("objectTypeConverter")
public class ObjectTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{defaultValuesSession}", DefaultValuesSessionMB.class);

        DefaultValuesSessionMB defaultValues = (DefaultValuesSessionMB) vex.getValue(context.getELContext());

        return defaultValues.getObjectTypes().stream()
                .filter(objectType -> objectType.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((ObjectType) value).getId());
    }
}
