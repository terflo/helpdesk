package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO implements Serializable {

    public Long id;

    public Long operator;

    public Long user;

    public RequestStatus status;

    public PriorityStatus priority;

    public String name;

    public String description;

    public Date date;

}
