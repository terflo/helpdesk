package com.terflo.helpdesk.model.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final List<String> badUsernames =
            Arrays.asList("admin", "administrator", "operator", "moderator");

    private static final Pattern VALID_USERNAME_REGEX =
            Pattern.compile("^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)(?!\\d+$)[a-zA-Z0-9.]{5,20}$");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s.isEmpty())
            return true;
        else {
            String username = s.toLowerCase().trim();
            return VALID_USERNAME_REGEX.matcher(username).find()    //обработка через Regex выражение
                    && !badUsernames.contains(username)             //поиск в запрещенных именах
                    && username.length() >= 5;                      //длинна строки не менее 5
        }
    }

}
