package com.example.recipes.util;

import java.util.regex.Pattern;

public class Validation {
    private static final String USERNAME_PATTERN = "^[a-zA-Z1-9]{5,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,}$"; //Minimum eight characters, at least one letter and one number

    public static boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        return pattern.matcher(username).matches();
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        return pattern.matcher(password).matches();

    }
}
