package com.terflo.helpdesk.model.requests;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * Запрос на верификацию данных для регистрации от клиента
 * @author Danil Krivoschiokov
 * @version 1.0
 */
@Data
public class RegistrationRequest {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * email пользователя
     */
    private String email;

    /**
     * Пароль пользователя
     */
    private String password;

    /**
     * Подтверждение пароля пользователя
     */
    private String passwordConfirm;

}
