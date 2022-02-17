package com.terflo.helpdesk.model.exceptions;

public class DecisionNameAlreadyExistsException extends Exception {

    public DecisionNameAlreadyExistsException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
