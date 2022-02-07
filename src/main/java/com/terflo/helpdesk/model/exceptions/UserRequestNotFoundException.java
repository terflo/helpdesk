package com.terflo.helpdesk.model.exceptions;

public class UserRequestNotFoundException extends Exception {

    public UserRequestNotFoundException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
