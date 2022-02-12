package com.terflo.helpdesk.model.exceptions;

public class UserRequestClosedException extends Exception {

    public UserRequestClosedException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
