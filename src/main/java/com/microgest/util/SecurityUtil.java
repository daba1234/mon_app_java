package com.microgest.util;

import org.mindrot.jbcrypt.BCrypt;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isBlank() || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}