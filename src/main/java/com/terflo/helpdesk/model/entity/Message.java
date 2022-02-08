package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Класс-сущность для хранения сообщений в базе данных
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    /**
     * Уникальный индификатор сообщения
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Отправитель сообщения
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    /**
     * Запрос пользователя, к которому относится сообщение
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private UserRequest userRequest;

    /**
     * Содержание сообщения
     */
    @Column(name="message")
    private String message;

    /**
     * Время отправки сообщения
     */
    @Column(name = "date")
    private Date date;

    /**
     * Статус доставки сообщения (доставлено/отправляется)
     */
    @Column(name = "status")
    private MessageStatus status;

}
