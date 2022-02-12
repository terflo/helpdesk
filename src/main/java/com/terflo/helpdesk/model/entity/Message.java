package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Класс-сущность для хранения сообщений в базе данных
 */
@Data
@Entity
@Transactional
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    /**
     * Уникальный индификатор сообщения
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Отправитель сообщения
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    /**
     * Запрос пользователя, к которому относится сообщение
     */
    @ManyToOne(fetch = FetchType.EAGER)
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