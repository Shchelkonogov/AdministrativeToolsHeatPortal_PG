package ru.tecon.admTools.systemParams.cdi.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.struct.StructMB;
import ru.tecon.admTools.systemParams.model.struct.SpHeader;

/**
 * Конвертер для названий справочников свойств структур
 * @author Maksim Shchelkonogov
 */
@FacesConverter("spHeaderConverter")
public class SpHeaderConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + component.getAttributes().get("bean") + "}", StructMB.class);

        StructMB divisions = (StructMB) vex.getValue(context.getELContext());

        return divisions.getSpHeaders().stream()
                .filter(spHeader -> {
                    if (value.equals("null")) {
                        return spHeader.getId() == null;
                    } else {
                        return (spHeader.getId() != null) && (spHeader.getId() == Integer.parseInt(value));
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((SpHeader) value).getId());
    }
}
