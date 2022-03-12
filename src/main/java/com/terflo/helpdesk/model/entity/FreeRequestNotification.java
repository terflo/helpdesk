package com.terflo.helpdesk.model.entity;

import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.entity.enums.FreeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeRequestNotification {

    private FreeRequestStatus freeRequestStatus;

    private Long userRequestID;

    private UserRequestDTO userRequestDTO;

}
