package com.terflo.helpdesk.model.entity.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDTO implements Serializable {

    public Long id;

    public String name;

    public String answer;

    public Date date;

    public UserDTO author;
}
