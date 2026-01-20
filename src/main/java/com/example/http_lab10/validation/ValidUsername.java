package com.example.http_lab10.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    String message() default "Invalid username (use 3-20 chars: letters, digits, underscore; must not start with digit)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
