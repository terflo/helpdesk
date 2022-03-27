package com.terflo.helpdesk.model.entity.dto;

import com.terflo.helpdesk.model.entity.enums.PriorityStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
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
    @Nullable
    public Long id;

    /**
     * Оператор запроса
     */
    @Nullable
    public UserDTO operator;

    /**
     * Отправитель запроса
     */
    @Nullable
    public UserDTO user;

    /**
     * Статус запроса
     */
    @Nullable
    public RequestStatus status;

    /**
     * Приоритет запроса
     */
    @NotNull(message = "Приоритет обращения не может быть пустым")
    public PriorityStatus priority;

    /**
     * Имя запроса
     */
    @NotBlank
    @Size(min = 10, max = 255, message = "Длина наименования обращения может быть от 10 до 255 символов")
    public String name;

    /**
     * Описание запроса
     */
    @NotBlank
    @Size(min = 10, max = 2048, message = "Длина описания обращения может быть от 10 до 2048 символов")
    public String description;

    /**
     * Дата создания запроса
     */
    @Nullable
    public Date date;
}
