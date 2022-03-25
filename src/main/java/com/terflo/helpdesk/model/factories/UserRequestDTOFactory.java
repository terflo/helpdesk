package com.terflo.helpdesk.model.factories;

import java.util.List;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.UserRequestServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс фабрика для создания сущностей "Запрос пользователя" (также в Data Transfer Object виде)
 * @author Danil Krivoschiokov
 * @version 1.3
 */
@Component
@AllArgsConstructor
public class UserRequestDTOFactory {

    private final UserRequestServiceImpl userRequestServiceImpl;

    private final UserDTOFactory userDTOFactory;

    public UserRequestDTO convertToUserRequestDTO(UserRequest userRequest) {
            return new UserRequestDTO(
                    userRequest.getId(),
                    userRequest.getOperator() != null ? userDTOFactory.convertToUserDTO(userRequest.getOperator()) : null,
                    userRequest.getUser() != null ? userDTOFactory.convertToUserDTO(userRequest.getUser()) : null,
                    userRequest.getStatus(),
                    userRequest.getPriority(),
                    userRequest.getName(),
                    userRequest.getDescription(),
                    userRequest.getDate()
            );
    }

    public UserRequest convertToUserRequest(UserRequestDTO userRequestDTO) {

        try {
            //Поиск запроса в базе данных
            UserRequest userRequest = userRequestServiceImpl.findUserRequestByID(userRequestDTO.id);

            //Если находим по id и сравнивание основных данных проходит успешно
            if(!Objects.equals(this.convertToUserRequestDTO(userRequest), userRequestDTO))
                throw new UserRequestNotFoundException("Обращение пользователя не найдено");
            else return userRequest;    //возвращаем обращение

        } catch (UserRequestNotFoundException | NullPointerException e) {

            //иначе создаем объект с такими же основными параметрами
            return new UserRequest(
                    userRequestDTO.id,
                    userRequestDTO.operator != null ? userDTOFactory.convertToUser(userRequestDTO.operator) : null,
                    userRequestDTO.user != null ? userDTOFactory.convertToUser(userRequestDTO.user) : null,
                    userRequestDTO.status,
                    userRequestDTO.priority,
                    userRequestDTO.name,
                    userRequestDTO.description,
                    userRequestDTO.date,
                    null
            );
        }
    }

    public List<UserRequestDTO> convertToUserRequestDTO(List<UserRequest> userRequest) {
        return userRequest
                .stream()
                .map(this::convertToUserRequestDTO)
                .collect(Collectors.toList());
    }
}
