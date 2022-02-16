package com.terflo.helpdesk.model.factory;

import java.util.List;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Класс фабрика для создания сущностей "Запрос пользователя" (также в Data Transfer Object виде)
 * @author Danil Krivoschiokov
 * @version 1.3
 */
@Component
public class UserRequestFactory {

    private final UserFactory userFactory;

    public UserRequestFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public UserRequestDTO convertToUserRequestDTO(UserRequest userRequest) {
            return new UserRequestDTO(
                    userRequest.getId(),
                    userRequest.getOperator() != null ? userFactory.convertToUserDTO(userRequest.getOperator()) : null,
                    userRequest.getUser() != null ? userFactory.convertToUserDTO(userRequest.getUser()) : null,
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
