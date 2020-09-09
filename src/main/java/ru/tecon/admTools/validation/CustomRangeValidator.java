package ru.tecon.admTools.validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named
@FacesValidator("rangeValidator")
public class CustomRangeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String tMin = (String) ((HtmlOutputText) context.getViewRoot().findComponent("graphSelectForm:gTab:tMin")).getValue();
        String tMax = (String) ((HtmlOutputText) context.getViewRoot().findComponent("graphSelectForm:gTab:tMax")).getValue();
        String aMin = (String) ((HtmlOutputText) context.getViewRoot().findComponent("graphSelectForm:gTab:aMin")).getValue();
        String aMax = (String) ((HtmlOutputText) context.getViewRoot().findComponent("graphSelectForm:gTab:aMax")).getValue();

        String optValue = (String) ((UIInput) context.getViewRoot().findComponent("graphSelectForm:gTab:optValue")).getValue();

        if (optValue.isEmpty()) {
            return;
        }

        try {
            double checkValue = Double.parseDouble(optValue);
            try {
                if ((checkValue < Double.parseDouble(tMin)) || (checkValue > Double.parseDouble(tMax))) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                            optValue + " выходит за гранциы"));
                }
            } catch (NumberFormatException e) {
                try {
                    if ((checkValue < Double.parseDouble(aMin)) || (checkValue > Double.parseDouble(aMax))) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                                optValue + " выходит за гранциы"));
                    }
                } catch (NumberFormatException ignore) {

                }
            }
        } catch (NumberFormatException ignore) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                    optValue + " не числовое значение"));
        }
    }
}
