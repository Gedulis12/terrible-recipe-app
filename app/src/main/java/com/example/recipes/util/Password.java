package com.example.recipes.util;

import org.mindrot.jbcrypt.BCrypt;

public class Password {
    public static String hashPassword(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashed;
    }

    public static boolean checkPassword(String candidate, String hashed) {
        if (candidate == null) {
            return false;
        } else {
            if (BCrypt.checkpw(candidate, hashed)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
