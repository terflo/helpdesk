package com.terflo.helpdesk.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexUtil {

    /**
     * Regex для валидации email пользователя
     */
    private final Pattern VALID_EMAIL_ADDRESS_REGEX;

    public RegexUtil() {
        this.VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    }

    public boolean checkEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }

}
