package ru.tecon.admTools.systemParams.cdi.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.MainParamMB;
import ru.tecon.admTools.systemParams.model.mainParam.TechProcParam;

/**
 * Конвертер для выбора параметров техпроцесса
 * @author Aleksey Sergeev
 */
@FacesConverter("parametrsTypeConverter")
public class TechProcParamConverter implements Converter<Object> {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{mainParamMB}", MainParamMB.class);

        MainParamMB defaultValues = (MainParamMB) vex.getValue(context.getELContext());

        return defaultValues.getParamsOfTechProcessList().stream()
                .filter(paramType -> paramType.getTechprid() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((TechProcParam) value).getTechprid());
    }
}
