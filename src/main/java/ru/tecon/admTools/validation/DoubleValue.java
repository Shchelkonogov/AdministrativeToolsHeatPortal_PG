package ru.tecon.admTools.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = BordersValidation.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleValue {

    String message() default "{DoubleValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
