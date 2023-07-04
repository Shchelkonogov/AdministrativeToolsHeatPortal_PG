package ru.tecon.admTools.systemParams.cdi.converter.genModel;

import ru.tecon.admTools.systemParams.cdi.GenModelMB;
import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

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

        GenModelMB genModelMB = (GenModelMB) vex.getValue(context.getELContext());
        int index = (int) component.getAttributes().get("index");


        return genModelMB.getNewPropCalc().getProps().get(index).getStatAggrListForChoice().stream()
                .filter(statAggrTable -> statAggrTable.getId() == Integer.parseInt(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((StatAggrTable) value).getId());
    }
}
