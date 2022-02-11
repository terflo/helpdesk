package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.repositories.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Класс фабрика для создания сущностей "Запрос пользователя" (также в Data Transfer Object виде)
 * @author Danil Krivoschiokov
 * @version 1.2
 */
@Component
public class UserRequestFactory {

    private final UserRepository userRepository;

    public UserRequestFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRequest convertToUserRequest(UserRequestDTO userRequestDTO) {

            return new UserRequest(
                    userRequestDTO.id,
                    userRepository.findById(userRequestDTO.operator).orElse(null),
                    userRepository.findById(userRequestDTO.user).orElse(null),
                    userRequestDTO.status,
                    userRequestDTO.priority,
                    userRequestDTO.name,
                    userRequestDTO.description,
                    userRequestDTO.date
            );
    }

    public UserRequestDTO convertToUserRequestDTO(UserRequest userRequest) {

            return new UserRequestDTO(
                    userRequest.getId(),
                    userRequest.getOperator() != null ? userRequest.getOperator().getId() : null,
                    userRequest.getUser() != null ? userRequest.getUser().getId() : null,
                    userRequest.getStatus(),
                    userRequest.getPriority(),
                    userRequest.getName(),
                    userRequest.getDescription(),
                    userRequest.getDate()
            );
    }
}
