package ru.tecon.admTools.specificModel.cdi;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

/**
 * Валидатор который проверяет, что бы оптимальное знаечние попадало между границ
 */
@Named
@FacesValidator("rangeValidator")
public class CustomRangeValidator implements Validator<Object> {

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
