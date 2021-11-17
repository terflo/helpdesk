package com.terflo.helpdesk.model.responses;

import lombok.Data;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Ответ на запрос верификации данных для регистрации
 */
@Data
public class RegistrationResponse {

    /**
     * Статус верификации имени пользователя
     */
    private String usernameStatus;

    /**
     * Статус верификации email пользователя
     */
    private String emailStatus;

    /**
     * Статус верификации пароля пользователя
     */
    private String passwordStatus;

}
