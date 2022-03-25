package com.terflo.helpdesk.model.entity.enums;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Статус запросов пользователей (в процессе, закрыто, начато)
 */
public enum RequestStatus {
    IN_PROCESS("В процессе"),
    CLOSED("Закрыто"),
    BEGINNING("Начато");

    private final String name;

    RequestStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
