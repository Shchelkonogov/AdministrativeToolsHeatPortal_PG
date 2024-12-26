package ru.tecon.admTools.systemParams.cdi.converter.paramTypeSetting;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.ParamTypeSettingMB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.ParamType;

/**
 * @author Maksim Shchelkonogov
 * 01.03.2023
 */
@FacesConverter("paramTypeConverter")
public class ParamTypeConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{paramTypeSetting}", ParamTypeSettingMB.class);

        ParamTypeSettingMB paramTypeSetting = (ParamTypeSettingMB) vex.getValue(context.getELContext());

        return paramTypeSetting.getTypesList().stream()
                .filter(paramType -> {
                    try {
                        if (paramType.getId() == Integer.parseInt(value)) {
                            return true;
                        }
                    } catch (NumberFormatException ignore) {
                    }
                    return false;
                })
                .findFirst()
                .orElse(new ParamType(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((ParamType) value).getId());
    }
}
