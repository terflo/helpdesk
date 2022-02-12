package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность для транспортировки информации о сообщении по сети
 * (Шаблон Data Transfer Object)
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    /**
     * ID Отправителя сообщения
     */
    public Long sender;

    /**
     * ID Запроса пользователя, к которому относится сообщение
     */
    public Long userRequest;

    /**
     * Содержание сообщения
     */
    public String message;

    /**
     * Время отправки сообщения
     */
    public Date date;

    /**
     * Статус доставки сообщения (доставлено/отправляется)
     */
    public MessageStatus status;

}
