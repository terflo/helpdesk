package com.terflo.helpdesk.model.entity.enums;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Статусы приоритетов (Низкий, Средний, Максимальный)
 */
public enum PriorityStatus {
    LOW("Низкий"),
    NORMAL("Средний"),
    MAX("Максимальный");

    private final String name;

    PriorityStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
