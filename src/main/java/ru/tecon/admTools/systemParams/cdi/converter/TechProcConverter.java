package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.MainParamMB;
import ru.tecon.admTools.systemParams.model.mainParam.TechProc;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для выбора техпроцесса
 * @author Aleksey Sergeev
 */
@FacesConverter("rightTypeConverter")
public class TechProcConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{mainParamMB}", MainParamMB.class);

        MainParamMB defaultValues = (MainParamMB) vex.getValue(context.getELContext());

        return defaultValues.getRightPartSelectOneMenuParam().stream()
                .filter(rightType -> rightType.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value==null){
            return null;
        }
        else
        return String.valueOf(((TechProc) value).getId());
    }

}
