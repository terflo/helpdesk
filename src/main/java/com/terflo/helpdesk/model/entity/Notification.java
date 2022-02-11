package com.terflo.helpdesk.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    /**
     *  ID нового сообщения
     */
    private Long messageID;

    /**
     * ID запроса пользователя, откуда пришло сообщение
     */
    private Long userRequestID;

    /**
     * ID отправителя сообщения
     */
    private Long senderID;
}
