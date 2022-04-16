package com.terflo.helpdesk.model.entity.enums;

public enum VerificationTypeToken {

    CHANGE_PASSWORD("Изменение пароля"),
    REGISTRATION_CONFIRM("Подтверждение регистрации");

    private final String name;

    VerificationTypeToken(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
