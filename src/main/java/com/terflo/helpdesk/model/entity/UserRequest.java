package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

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
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private User operator;

    /**
     * Владелец обращения
     */
    @ManyToOne(fetch = FetchType.EAGER)
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

    /**
     * Дата создания запроса
     */
    @Column(name="date")
    private Date date;

}
