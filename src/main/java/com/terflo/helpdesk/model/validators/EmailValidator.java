package com.terflo.helpdesk.model.validators;

import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Autowired
    private UserService userService;

    private final static Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if(s.isEmpty()) {
            return true;
        } else if(!VALID_EMAIL_ADDRESS_REGEX.matcher(s).find()) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Некорректный email")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        } else if(userService.userIsExistByEmail(s)) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Такой email уже зарегестрирован")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        return true;
    }
}
