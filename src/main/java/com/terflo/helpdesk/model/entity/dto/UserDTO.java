package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.validators.Email;
import com.terflo.helpdesk.model.validators.Username;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Сущность для транспортировки информации о пользователе по сети
 * (Шаблон Data Transfer Object)
 */
@ToString
@EqualsAndHashCode
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
    @Username
    public String username;

    /**
     * Email пользователя
     */
    @Email
    public String email;

    /**
     * Список ролей пользователя
     */
    public Set<String> roles;

    /**
     * Описание пользователя
     */
    public String description;

    /**
     * Дата регистрации пользователя
     */
    public Date date;

}
