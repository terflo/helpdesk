package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDTOFactory {

    /**
     * Метод преобразует объект User в UserDTO
     * @param user преобразуемый объект
     * @return преобразованный user
     */
    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map((Role::getName))
                        .collect(Collectors.toSet()),
                user.getDescription()
        );
    }

    /**
     * Метод преобразует список из User в список из UserDTO
     * @param users список пользователей
     * @return список конвертированных пользователей
     */
    public List<UserDTO> convertToUserDTO(List<User> users) {
        return users
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

}
