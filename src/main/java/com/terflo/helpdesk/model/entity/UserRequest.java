package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Обращение пользователя для связи с сотрудником тех-поддержки
 */
@Data
@Entity
@Transactional
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
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
    @JoinColumn(name = "operator_id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private User operator;

    /**
     * Владелец обращения
     */
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private User user;

    /**
     * Статус обработки обращения
     */
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    /**
     * Приоритет задачи
     */
    @Column(name = "priority", nullable = false)
    private PriorityStatus priority;

    /**
     * Название проблемы
     */
    @Column(name= "name", nullable = false)
    private String name;

    /**
     * Описание проблемы
     */
    @Column(name="description", nullable = true)
    private String description;

    /**
     * Дата создания запроса
     */
    @Column(name="date", nullable = false)
    private Date date;

    /**
     * Сообщения в обращении
     */
    @JoinColumn(name = "user_request_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messages;
}
