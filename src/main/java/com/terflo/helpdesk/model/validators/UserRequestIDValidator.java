package com.terflo.helpdesk.model.validators;

import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserRequestIDValidator implements ConstraintValidator<UserRequestID, Long> {

    @Autowired
    UserRequestService userRequestService;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {

        if(id < 0) return false;

        try {
            userRequestService.findUserRequestByID(id);
        } catch (UserRequestNotFoundException e) {
            return false;
        }

        return true;
    }
}
