package ru.tecon.admTools.systemParams.cdi.converter.paramTypeSetting;

import ru.tecon.admTools.systemParams.cdi.ParamTypeSettingMB;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.StatisticalAggregate;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Maksim Shchelkonogov
 * 07.03.2023
 */
@FacesConverter("statAggregateConverter")
public class StatisticalAggregateConverter implements Converter {

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
