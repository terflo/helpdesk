package com.terflo.helpdesk.model.exceptions;

public class UserAlreadyActivatedException extends Exception {

    public UserAlreadyActivatedException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
