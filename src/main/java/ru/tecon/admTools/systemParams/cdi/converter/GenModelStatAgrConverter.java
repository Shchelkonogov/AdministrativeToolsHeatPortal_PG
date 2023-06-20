package ru.tecon.admTools.systemParams.cdi.converter;

import ru.tecon.admTools.systemParams.cdi.GenModelMB;
import ru.tecon.admTools.systemParams.model.genModel.CalcAgrVars;
import ru.tecon.admTools.systemParams.model.genModel.StatAgrList;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Конвертер для выбора типа объекта
 * @author Aleksey Sergeev
 */
@FacesConverter("genModelStatAgrConverter")
public class GenModelStatAgrConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{genModelMB}", GenModelMB.class);

        GenModelMB defaultValues = (GenModelMB) vex.getValue(context.getELContext());
        String val = String.valueOf(value.charAt(0));
        for (CalcAgrVars i: defaultValues.getCalcAgrVarsList()) {
            if (i.getVariable().equals(val)) {
                return i.getRowList().stream()
                        .filter(leftType -> leftType.getVariable().equals(value))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((StatAgrList) value).getVariable());
    }
}
