package ru.tecon.admTools.systemParams.cdi.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.model.struct.PropValType;

/**
 * Конвертер для типов свойств структур
 * @author Maksim Shchelkonogov
 */
@FacesConverter("propValTypeConverter")
public class PropValTypeConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + component.getAttributes().get("bean") + "}", MyConverter.class);

        MyConverter divisions = (MyConverter) vex.getValue(context.getELContext());

        return divisions.getPropValTypes().stream()
                .filter(propValType -> propValType.getCode().equals(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((PropValType) value).getCode();
    }
}
