package com.terflo.helpdesk.model.validators;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

@Log4j2
public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatcher, Object> {

    private String password;

    private String passwordConfirm;

    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
        password = constraintAnnotation.first();
        passwordConfirm = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {

            final String firstObj = BeanUtils.getProperty(o, password);
            final String secondObj = BeanUtils.getProperty(o, passwordConfirm);

            if(!(firstObj instanceof String) || !(secondObj instanceof String))
                throw new IllegalArgumentException("Аннотация @PasswordMatcher поддерживает только тип String");

            if(firstObj.isEmpty()) {    //проверка на пустоту поля
                return true;
            } else if(firstObj.length() < 5) {   //проверка на размер пароля

                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Пароль слишком короткий")
                        .addPropertyNode(password)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                return false;

            } else if(!firstObj.equals(secondObj)) {  //Проверка на совпадение паролей

                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Пароли не совпадают")
                        .addPropertyNode(passwordConfirm)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                return false;

            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage());
        }

        return true;
    }
}
