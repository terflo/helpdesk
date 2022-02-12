package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность для транспортировки информации о запросе пользователя по сети
 * (Шаблон Data Transfer Object)
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO implements Serializable {

    /**
     * ID запроса
     */
    public Long id;

    /**
     * Оператор запроса
     */
    public UserDTO operator;

    /**
     * Создать запроса
     */
    public UserDTO user;

    /**
     * Статус запроса
     */
    public RequestStatus status;

    /**
     * Приоритет запроса
     */
    public PriorityStatus priority;

    /**
     * Имя запроса
     */
    public String name;

    /**
     * Описание запроса
     */
    public String description;

    /**
     * Дата создания запроса
     */
    public Date date;

}
