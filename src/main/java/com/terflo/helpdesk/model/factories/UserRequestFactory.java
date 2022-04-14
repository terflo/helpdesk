package com.terflo.helpdesk.model.factories;

import java.io.IOException;
import java.util.List;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import com.terflo.helpdesk.utils.LambdaExceptionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс фабрика для создания сущностей "Запрос пользователя" (также в Data Transfer Object виде)
 * @author Danil Krivoschiokov
 * @version 1.5
 */
@Component
@AllArgsConstructor
public class UserRequestFactory {

    private final UserRequestService userRequestService;

    private final ImageFactory imageFactory;

    private final UserFactory userFactory;

    public UserRequestDTO convertToUserRequestDTO(UserRequest userRequest) {
            return new UserRequestDTO(
                    userRequest.getId(),
                    userRequest.getOperator() != null ? userFactory.convertToUserDTO(userRequest.getOperator()) : null,
                    userRequest.getUser() != null ? userFactory.convertToUserDTO(userRequest.getUser()) : null,
                    userRequest.getStatus(),
                    userRequest.getPriority(),
                    userRequest.getName(),
                    userRequest.getDescription(),
                    userRequest.getDate(),
                    userRequest.getImages() != null ? userRequest.getImages()
                            .stream()
                            .map(Image::getId)
                            .collect(Collectors.toList()) : null,
                    null
            );
    }

    public UserRequestDTO convertToUserRequestDTOWithBase64Images(UserRequest userRequest) {
        return new UserRequestDTO(
                userRequest.getId(),
                userRequest.getOperator() != null ? userFactory.convertToUserDTO(userRequest.getOperator()) : null,
                userRequest.getUser() != null ? userFactory.convertToUserDTO(userRequest.getUser()) : null,
                userRequest.getStatus(),
                userRequest.getPriority(),
                userRequest.getName(),
                userRequest.getDescription(),
                userRequest.getDate(),
                userRequest.getImages() != null ? userRequest.getImages()
                        .stream()
                        .map(Image::getId)
                        .collect(Collectors.toList()) : null,
                userRequest.getImages() != null ? userRequest.getImages()
                        .stream()
                        .map(Image::getBase64ImageWithType)
                        .collect(Collectors.toList()) : null
        );
    }

    public UserRequest convertToUserRequest(UserRequestDTO userRequestDTO) throws IOException {

        try {
            //Поиск запроса в базе данных
            UserRequest userRequest = userRequestService.findUserRequestByID(Objects.requireNonNull(userRequestDTO.id));

            //Если находим по id и сравнивание основных данных проходит успешно
            if(!Objects.equals(this.convertToUserRequestDTO(userRequest), userRequestDTO))
                throw new UserRequestNotFoundException("Обращение пользователя не найдено");
            else return userRequest;    //возвращаем обращение

        } catch (UserRequestNotFoundException | NullPointerException e) {

            //иначе создаем объект с такими же основными параметрами
            return new UserRequest(
                    userRequestDTO.id,
                    userRequestDTO.operator != null ? userFactory.convertToUser(userRequestDTO.operator) : null,
                    userRequestDTO.user != null ? userFactory.convertToUser(userRequestDTO.user) : null,
                    userRequestDTO.status,
                    userRequestDTO.priority,
                    userRequestDTO.name,
                    userRequestDTO.description,
                    userRequestDTO.date,
                    null,
                    userRequestDTO.imagesBase64 != null ? userRequestDTO.imagesBase64
                            .stream()
                            .map(LambdaExceptionUtil.rethrowFunction(imageFactory::getImage))
                            .collect(Collectors.toList()) : null
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
