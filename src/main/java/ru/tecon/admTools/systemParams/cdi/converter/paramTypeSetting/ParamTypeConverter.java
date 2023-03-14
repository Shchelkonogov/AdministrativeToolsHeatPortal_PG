package ru.tecon.admTools.systemParams.cdi.converter.paramTypeSetting;

import ru.tecon.admTools.systemParams.cdi.ParamTypeSettingMB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.ParamType;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Maksim Shchelkonogov
 * 01.03.2023
 */
@FacesConverter("paramTypeConverter")
public class ParamTypeConverter implements Converter {

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
