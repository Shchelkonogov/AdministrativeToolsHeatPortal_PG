package ru.tecon.admTools.systemParams.cdi.converter.paramTypeSetting;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.ParamTypeSettingMB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.StatisticalAggregate;

/**
 * @author Maksim Shchelkonogov
 * 07.03.2023
 */
@FacesConverter("statAggregateConverter")
public class StatisticalAggregateConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{paramTypeSetting}", ParamTypeSettingMB.class);

        ParamTypeSettingMB paramTypeSetting = (ParamTypeSettingMB) vex.getValue(context.getELContext());

        return paramTypeSetting.getStatAggregates().stream()
                .filter(statisticalAggregate -> statisticalAggregate.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((StatisticalAggregate) value).getId());
    }
}
