package com.terflo.helpdesk.model.exceptions;

public class UserRequestAlreadyHaveOperatorException extends Exception {

    public UserRequestAlreadyHaveOperatorException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
