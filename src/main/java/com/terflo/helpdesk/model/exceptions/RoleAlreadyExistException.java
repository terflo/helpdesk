package com.terflo.helpdesk.model.exceptions;

public class RoleAlreadyExistException extends Exception {

    public RoleAlreadyExistException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
