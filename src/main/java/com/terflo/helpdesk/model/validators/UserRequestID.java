package com.terflo.helpdesk.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target( { FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UserRequestIDValidator.class)
public @interface UserRequestID {

    String message() default "Некорректный индификатор обращения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
