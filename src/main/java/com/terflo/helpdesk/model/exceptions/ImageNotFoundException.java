package com.terflo.helpdesk.model.exceptions;

public class ImageNotFoundException extends Exception {

    public ImageNotFoundException(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
