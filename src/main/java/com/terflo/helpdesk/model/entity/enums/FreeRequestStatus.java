package com.terflo.helpdesk.model.entity.enums;

/**
 * Статус нового обращения
 */
public enum FreeRequestStatus {
    NEW("Новый"),
    ACCEPTED("Принят");

    private final String name;

    FreeRequestStatus(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
