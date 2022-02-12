package com.terflo.helpdesk.model.entity.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сущность для транспортировки информации о пользователе по сети
 * (Шаблон Data Transfer Object)
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    /**
     * ID пользователя
     */
    public Long id;

    /**
     * Имя пользователя
     */
    public String username;

    /**
     * Email пользователя
     */
    public String email;

    /**
     * Список ролей пользователя
     */
    public Set<String> roles;

}
