package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Обращение пользователя для связи с сотрудником тех-поддержки
 */
@Data
@Entity
@Table(name = "users_requests")
public class UserRequest {

    /**
     * Уникальный индификатор чат-комнаты
     */
    @Id
    @Column(name="id")
    private Long id;

    /**
     * Оператор тех-поддержки в чате
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private User operator;

    /**
     * Пользователь в чате
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * Статус обработки сообщения
     */
    @Column(name = "status")
    private RequestStatus status;

    /**
     * Приоритет задачи
     */
    @Column(name = "priority")
    private PriorityStatus priority;
}
