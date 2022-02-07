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
     * Уникальный индификатор обращения пользователя
     */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Оператор тех-поддержки, прикрепленный к обращению
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private User operator;

    /**
     * Владелец обращения
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * Статус обработки обращения
     */
    @Column(name = "status")
    private RequestStatus status;

    /**
     * Приоритет задачи
     */
    @Column(name = "priority")
    private PriorityStatus priority;

    /**
     * Название проблемы
     */
    @Column(name= "name")
    private String name;

    /**
     * Описание проблемы
     */
    @Column(name="description")
    private String description;

}
