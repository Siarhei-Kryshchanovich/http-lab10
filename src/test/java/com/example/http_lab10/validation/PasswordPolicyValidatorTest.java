package com.example.http_lab10.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyValidatorTest {

    private final PasswordPolicyValidator validator = new PasswordPolicyValidator();

    @Test
    void validPassword_isAccepted() {
        assertTrue(validator.isValid("Abcdef1!23", null));
        assertTrue(validator.isValid("Str0ng#Password2026", null));
    }

    @Test
    void invalidPassword_rejected_whenNullOrWhitespace() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("Abc def1!23", null));
    }

    @Test
    void invalidPassword_rejected_whenTooShortOrTooLong() {
        assertFalse(validator.isValid("Ab1!cdef", null));
        assertFalse(validator.isValid("A".repeat(101), null));
    }

    @Test
    void invalidPassword_rejected_whenCommon() {
        assertFalse(validator.isValid("password", null));
        assertFalse(validator.isValid("Admin123", null));
    }

    @Test
    void invalidPassword_rejected_whenMissingRequiredClasses() {
        assertFalse(validator.isValid("abcdef1!23", null));
        assertFalse(validator.isValid("ABCDEF1!23", null));
        assertFalse(validator.isValid("Abcdefg!hi", null));
        assertFalse(validator.isValid("Abcdef1234", null));
    }
}