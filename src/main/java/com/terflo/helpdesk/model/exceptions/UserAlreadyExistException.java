package com.terflo.helpdesk.model.exceptions;

public class UserAlreadyExistException extends Exception {

    public UserAlreadyExistException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
