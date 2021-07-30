package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.StructMB;
import ru.tecon.admTools.systemParams.model.struct.PropCat;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для категорий свойств структур
 * @author Maksim Shchelkonogov
 */
@FacesConverter("propCatConverter")
public class PropCatConverter implements Converter {

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
