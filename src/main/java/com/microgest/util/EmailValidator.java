package com.microgest.util;

import java.util.regex.Pattern;

public final class EmailValidator {

    private static final Pattern PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private EmailValidator() {
    }

    public static boolean isValid(String email) {
        return email != null && PATTERN.matcher(email.trim()).matches();
    }
}