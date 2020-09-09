package ru.tecon.admTools.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BordersValidation implements ConstraintValidator<DoubleValue, Double> {

    @Override
    public void initialize(DoubleValue constraintAnnotation) {

    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        try {
//            if (value.length() != 0) {
//                Double.parseDouble(value);
//            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
