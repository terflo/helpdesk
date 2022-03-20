package com.terflo.helpdesk.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
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
     * Список запрещенных имен пользователей
     */
    private final List<String> badUsernames = Arrays.asList(
            "admin", "administrator", "operator", "moderator"
    );

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
     * путем сравнивания ника через Regex и поиска в списке запрещенных
     * @param username строка с username пользователя
     * @return результат валидации строки
     */
    public boolean checkUsername(String username) {
        return VALID_USERNAME_REGEX.matcher(username).find() && !badUsernames.contains(username.toLowerCase());
    }

}
