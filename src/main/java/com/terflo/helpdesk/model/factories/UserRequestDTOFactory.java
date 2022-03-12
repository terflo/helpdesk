package com.terflo.helpdesk.model.factories;

import java.util.List;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Класс фабрика для создания сущностей "Запрос пользователя" (также в Data Transfer Object виде)
 * @author Danil Krivoschiokov
 * @version 1.3
 */
@Component
@AllArgsConstructor
public class UserRequestDTOFactory {

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

    public List<UserRequestDTO> convertToUserRequestDTO(List<UserRequest> userRequest) {
        return userRequest
                .stream()
                .map(this::convertToUserRequestDTO)
                .collect(Collectors.toList());
    }
}
