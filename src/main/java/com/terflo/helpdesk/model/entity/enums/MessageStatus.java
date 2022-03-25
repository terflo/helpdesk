package com.terflo.helpdesk.model.entity.enums;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Статусы сообщений (отправлено, доставлено)
 */
public enum MessageStatus {
    RECEIVED("Отправлено"),
    DELIVERED("Доставлено");

    private final String name;

    MessageStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
