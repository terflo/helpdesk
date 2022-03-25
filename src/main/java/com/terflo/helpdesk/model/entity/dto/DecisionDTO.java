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

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDTO implements Serializable {

    @Nullable
    public Long id;

    @Size(min = 10, max = 255, message = "Минимальная длина наименования 10, максимальная 255")
    @NotBlank(message = "Наименование должно иметь содержание")
    public String name;

    @Size(min = 10, max = 2048, message = "Минимальная длина ответа 10, максимальная 2048")
    @NotBlank(message = "Ответ должен иметь содержание")
    public String answer;

    @Nullable
    public Date date;

    @Nullable
    public UserDTO author;

    public String toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
