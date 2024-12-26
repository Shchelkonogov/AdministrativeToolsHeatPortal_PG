package ru.tecon.admTools.systemParams.cdi.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.struct.StructMB;
import ru.tecon.admTools.systemParams.model.struct.PropCat;

/**
 * Конвертер для категорий свойств структур
 * @author Maksim Shchelkonogov
 */
@FacesConverter("propCatConverter")
public class PropCatConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + component.getAttributes().get("bean") + "}", StructMB.class);

        StructMB divisions = (StructMB) vex.getValue(context.getELContext());

        return divisions.getPropCat().stream()
                .filter(propCat -> propCat.getId().equals(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((PropCat) value).getId();
    }
}
