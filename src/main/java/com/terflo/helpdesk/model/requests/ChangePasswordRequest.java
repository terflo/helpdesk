package com.terflo.helpdesk.model.requests;

import com.terflo.helpdesk.model.validators.PasswordMatcher;
import com.terflo.helpdesk.model.validators.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@PasswordMatcher
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotNull(message = "Имя пользователь не может быть пустым")
    private String username;

    @NotNull(message = "Пароль не может быть пустым")
    private String password;

    @NotNull(message = "Подтверждение пароля не может быть пустым")
    private String passwordConfirm;

    @NotNull(message = "Код активации не может быть пустым")
    private String activateCode;
}
