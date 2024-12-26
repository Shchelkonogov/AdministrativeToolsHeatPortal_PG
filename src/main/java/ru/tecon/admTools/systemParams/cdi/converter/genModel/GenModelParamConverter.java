package ru.tecon.admTools.systemParams.cdi.converter.genModel;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.GenModelMB;
import ru.tecon.admTools.systemParams.model.genModel.ObjectParam;

/**
 * Конвертер для выбора типа объекта
 * @author Aleksey Sergeev
 */
@FacesConverter("genModelParamConverter")
public class GenModelParamConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{genModelMB}", GenModelMB.class);

        GenModelMB genModelMB = (GenModelMB) vex.getValue(context.getELContext());

        return genModelMB.getNewPropCalc().getParamListForChoice().stream()
                .filter(param -> param.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((ObjectParam) value).getId());
    }
}
