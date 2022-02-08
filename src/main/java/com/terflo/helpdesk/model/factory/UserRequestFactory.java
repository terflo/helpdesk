package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRequestFactory {

    @Autowired
    UserRequestService userRequestService;

    @Autowired
    UserService userService;

    public UserRequest convertToUserRequest(UserRequestDTO userRequestDTO) throws UserNotFoundException {
        return new UserRequest(
                userRequestDTO.id,
                userService.findUserById(userRequestDTO.operator),
                userService.findUserById(userRequestDTO.user),
                userRequestDTO.status,
                userRequestDTO.priority,
                userRequestDTO.name,
                userRequestDTO.description,
                userRequestDTO.date
        );
    }
}
