package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
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
    @JoinColumn(name = "operator_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private User operator;

    /**
     * Владелец обращения
     */
    @JoinColumn(name = "user_id")
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
    @Column(name="description", length = 2048)
    private String description;

    /**
     * Дата создания запроса
     */
    @Column(name="date", nullable = false)
    private Date date;

    /**
     * Сообщения в обращении
     */
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "userRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<Message> messages;
}
