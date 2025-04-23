package ru.tecon.admTools.mobile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.tecon.admTools.mobile.model.UserObject;

/**
 * @author Maksim Shchelkonogov
 * 22.04.2025
 */
@Named
@ApplicationScoped
@FacesConverter(value = "userObjectConverter", managed = true)
public class UserObjectConverter implements Converter<UserObject> {

    @Inject
    private TechParamService techParamService;

    @Override
    public UserObject getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                return techParamService.getUserObjectMap().get(Integer.parseInt(value));
            }
            catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid userObject."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, UserObject value) {
        if (value != null) {
            return String.valueOf(value.getId());
        }
        else {
            return null;
        }
    }
}
