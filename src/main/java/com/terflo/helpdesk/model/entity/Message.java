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
 * @version 1.1
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
    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    /**
     * Запрос пользователя, к которому относится сообщение
     */
    @JoinColumn(name = "request_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserRequest userRequest;

    /**
     * Содержание сообщения
     */
    @Column(name="message", nullable = false)
    private String message;

    /**
     * Время отправки сообщения
     */
    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Статус доставки сообщения (доставлено/отправляется)
     */
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    /**
     * Изображение в сообщении
     */
    @JoinColumn(name = "image_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Image image;

}
