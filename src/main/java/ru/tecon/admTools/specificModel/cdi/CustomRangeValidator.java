package ru.tecon.admTools.specificModel.cdi;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 * Валидатор который проверяет, что бы оптимальное знаечние попадало между границ
 */
@Named
@FacesValidator("rangeValidator")
public class CustomRangeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String tMin = (String) ((HtmlOutputText) context.getViewRoot().findComponent("optValueSelectForm:tMin")).getValue();
        String tMax = (String) ((HtmlOutputText) context.getViewRoot().findComponent("optValueSelectForm:tMax")).getValue();
        String aMin = (String) ((HtmlOutputText) context.getViewRoot().findComponent("optValueSelectForm:aMin")).getValue();
        String aMax = (String) ((HtmlOutputText) context.getViewRoot().findComponent("optValueSelectForm:aMax")).getValue();

        String optValue = (String) ((UIInput) context.getViewRoot().findComponent("optValueSelectForm:optValue")).getValue();

        if (optValue.isEmpty()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                    "Нельзя задать пустое значение"));
        }

        try {
            double checkValue = Double.parseDouble(optValue);

            if (checkDown(checkValue, aMin) || checkDown(checkValue, tMin) || checkUp(checkValue, tMax) || checkUp(checkValue, aMax)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                        optValue + " выходит за гранциы"));
            }
        } catch (NumberFormatException ignore) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка проверки",
                    optValue + " не числовое значение"));
        }
    }

    private boolean checkUp(Double value, String referentValue) {
        if (referentValue.equals("-")) {
            return false;
        }

        return !(value <= Double.parseDouble(referentValue));
    }

    private boolean checkDown(Double value, String referentValue) {
        if (referentValue.equals("-")) {
            return false;
        }

        return !(value >= Double.parseDouble(referentValue));
    }
}
