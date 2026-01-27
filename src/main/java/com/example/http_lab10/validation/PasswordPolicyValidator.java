package com.example.http_lab10.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class PasswordPolicyValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Set<String> COMMON = Set.of(
            "password", "password1", "qwerty", "qwerty123", "1234567890", "12345678", "11111111", "admin", "admin123"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        if (value.contains(" ")) return false;
        if (value.length() < 10 || value.length() > 100) return false;

        String lower = value.toLowerCase();
        if (COMMON.contains(lower)) return false;

        boolean hasUpper = value.matches(".*[A-Z].*");
        boolean hasLower = value.matches(".*[a-z].*");
        boolean hasDigit = value.matches(".*\\d.*");
        boolean hasSpecial = value.matches(".*[^A-Za-z0-9].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
