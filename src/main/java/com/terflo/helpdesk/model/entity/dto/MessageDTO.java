package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.validators.UserRequestID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Сущность для транспортировки информации о сообщении по сети
 * (Шаблон Data Transfer Object)
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    /**
     * UserDTO объект отправителя сообщения
     */
    @NotNull
    public UserDTO sender;

    /**
     * ID Запроса пользователя, к которому относится сообщение
     */
    @UserRequestID
    public Long userRequest;

    /**
     * Содержание сообщения
     */
    @Size(max = 255, message = "Длина сообщения не должна превышать длину в 255 символов")
    public String message;

    /**
     * Время отправки сообщения
     */
    @Nullable
    public Date date;

    /**
     * Статус доставки сообщения (доставлено/отправляется)
     */
    @Nullable
    public MessageStatus status;

    /**
     * Base64 хэш изображения
     */
    @Nullable
    public String imageBase64;

}
