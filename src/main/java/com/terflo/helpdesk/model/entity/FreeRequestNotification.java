package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.entity.enums.FreeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Уведомление о новом свободном обращении
 * @author Danil Krivoschiokov
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeRequestNotification {

    /**
     * Статус нового обращения
     */
    private FreeRequestStatus freeRequestStatus;

    /**
     * Уникальный индификатор обращения
     */
    private Long userRequestID;

    /**
     * Информация об обращении
     */
    private UserRequestDTO userRequestDTO;

}
