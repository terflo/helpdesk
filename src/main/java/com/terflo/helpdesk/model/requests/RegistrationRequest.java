package com.terflo.helpdesk.model.requests;

import com.terflo.helpdesk.model.validators.Email;
import com.terflo.helpdesk.model.validators.PasswordMatcher;
import com.terflo.helpdesk.model.validators.Username;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Запрос на верификацию данных для регистрации от клиента
 * @author Danil Krivoschiokov
 * @version 1.2
 */
@Data
@PasswordMatcher
@AllArgsConstructor
public class RegistrationRequest {

    /**
     * Имя пользователя
     */
    @NotNull
    @Username
    private String username;

    /**
     * email пользователя
     */
    @NotNull
    @Email
    private String email;

    /**
     * Пароль пользователя
     */
    @NotNull
    private String password;

    /**
     * Подтверждение пароля пользователя
     */
    @NotNull
    private String passwordConfirm;
}
