package com.terflo.helpdesk.model.entity.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Сущность для транспортировки информации о часто задаваемых вопросов
 * (Шаблон Data Transfer Object)
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDTO implements Serializable {

    /**
     * Уникальный индификатор
     */
    @Nullable
    public Long id;

    /**
     * Наименование частого вопроса
     */
    @Size(min = 10, max = 255, message = "Длина наименования от 10 до 255 символов")
    @NotBlank(message = "Наименование должно иметь содержание")
    public String name;

    /**
     * Ответ на частый вопрос
     */
    @Size(min = 10, max = 2048, message = "Длина ответа от 10 до 2048 символов")
    @NotBlank(message = "Ответ должен иметь содержание")
    public String answer;

    /**
     * Дата создания
     */
    @Nullable
    public Date date;

    /**
     * Автор частого вопроса
     */
    @Nullable
    public UserDTO author;

    /**
     * Метод конвертации объекта в JSON строку
     * @return JSON вид объекта
     * @throws JsonProcessingException возникает в случае неудачи конвертации в JSON
     */
    public String toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
