package com.terflo.helpdesk.model.requests;

import com.terflo.helpdesk.model.validators.Email;
import com.terflo.helpdesk.model.validators.PasswordMatcher;
import com.terflo.helpdesk.model.validators.Username;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    @Username
    private String username;

    /**
     * email пользователя
     */
    @NotBlank
    @Email
    private String email;

    /**
     * Пароль пользователя
     */
    @NotBlank
    private String password;

    /**
     * Подтверждение пароля пользователя
     */
    @NotBlank
    private String passwordConfirm;
}
