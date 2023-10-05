package ru.tecon.admTools.linker.cdi.converter;

import ru.tecon.admTools.linker.cdi.scope.view.LinkerController;
import ru.tecon.admTools.linker.model.OpcObjectForLinkData;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Конвертер для определения id для таблицы "Нелинкованные объекты"
 *
 * @author Maksim Shchelkonogov
 * 02.08.2023
 */
public class OpcObjectForNoLinkDataConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ValueExpression vex = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{linkerController}", LinkerController.class);

        LinkerController linkerController = (LinkerController) vex.getValue(context.getELContext());
        return linkerController.getOpcObjectsForNoLinkData().getWrappedData().stream()
                .filter(opcObjectForLinkData -> opcObjectForLinkData.getId().equals(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((OpcObjectForLinkData) value).getId();
    }
}
