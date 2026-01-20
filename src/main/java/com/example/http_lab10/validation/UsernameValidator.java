package com.example.http_lab10.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        // 3-20 символов, буквы/цифры/_, не начинается с цифры
        return value.matches("^[A-Za-z_][A-Za-z0-9_]{2,19}$");
    }
}
