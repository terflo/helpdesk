package com.terflo.helpdesk.model.exceptions;

public class InvalidReCaptchaException extends Exception{

    public InvalidReCaptchaException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
