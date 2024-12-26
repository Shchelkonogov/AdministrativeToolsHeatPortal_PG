package ru.tecon.admTools.systemParams.cdi.converter;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import ru.tecon.admTools.systemParams.cdi.scope.view.ConditionController;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

/**
 * @author Maksim Shchelkonogov
 * 28.02.2023
 */
@FacesConverter("conditionConverter")
public class ConditionConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{conditionControllerView}", ConditionController.class);

        ConditionController conditionController = (ConditionController) vex.getValue(context.getELContext());

        return conditionController.getParamConditions().stream()
                .filter(condition -> condition.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((Condition) value).getId());
    }
}
