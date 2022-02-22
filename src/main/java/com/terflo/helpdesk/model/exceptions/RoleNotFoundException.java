package com.terflo.helpdesk.model.exceptions;

public class RoleNotFoundException extends Exception {

    public RoleNotFoundException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
