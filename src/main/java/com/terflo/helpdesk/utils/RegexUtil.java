package com.terflo.helpdesk.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * REGEX утилита для проверки строк
 */
@Component
public class RegexUtil {

    /**
     * Regex для валидации email пользователя
     */
    private final Pattern VALID_EMAIL_ADDRESS_REGEX;
    private final Pattern VALID_USERNAME_REGEX;

    /**
     * Конструктор класса, который инициализирует REGEX паттерны для валидации строк
     */
    public RegexUtil() {
        this.VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        this.VALID_USERNAME_REGEX = Pattern.compile("^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)(?!\\d+$)[a-zA-Z0-9.]{5,20}$");
    }

    /**
     * Метод, который проводит валидацию для строки email
     * @param email строка с email пользователя
     * @return результат валидации строки
     */
    public boolean checkEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }

    /**
     * Метод, который проводит валидацию для строки username
     * @param username строка с username пользователя
     * @return результат валидации строки
     */
    public boolean checkUsername(String username) {
        return VALID_USERNAME_REGEX.matcher(username).find();
    }

}
