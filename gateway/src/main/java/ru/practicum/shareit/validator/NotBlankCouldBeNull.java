package ru.practicum.shareit.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomNotBlankValidator.class)
public @interface NotBlankCouldBeNull {
    String message() default "Field must be null or must not be not blank";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
