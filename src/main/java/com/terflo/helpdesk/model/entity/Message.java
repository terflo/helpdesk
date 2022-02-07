package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.MessageStatus;
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
     * Уникальный индификатор отправителя сообщения
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    /**
     * Уникальный индификатор получателя сообщения
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;

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
