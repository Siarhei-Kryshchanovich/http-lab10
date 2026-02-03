package com.example.http_lab10.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameValidatorTest {

    private final UsernameValidator validator = new UsernameValidator();

    @Test
    void validUsername_isAccepted() {
        assertTrue(validator.isValid("User_1", null));
        assertTrue(validator.isValid("abc", null));
        assertTrue(validator.isValid("_user_name_20", null));
    }

    @Test
    void invalidUsername_rejected_whenNullOrTooShortOrTooLong() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("ab", null));
        assertFalse(validator.isValid("a".repeat(21), null));
    }

    @Test
    void invalidUsername_rejected_whenStartsWithDigitOrBadChars() {
        assertFalse(validator.isValid("1user", null));
        assertFalse(validator.isValid("user-name", null));
        assertFalse(validator.isValid("user name", null));
    }
}