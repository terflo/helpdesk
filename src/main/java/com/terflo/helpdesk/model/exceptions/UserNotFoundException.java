package com.terflo.helpdesk.model.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
