package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.scope.view.ConditionController;
import ru.tecon.admTools.systemParams.model.paramTypeSetting.Condition;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Maksim Shchelkonogov
 * 28.02.2023
 */
@FacesConverter("conditionConverter")
public class ConditionConverter implements Converter {

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
