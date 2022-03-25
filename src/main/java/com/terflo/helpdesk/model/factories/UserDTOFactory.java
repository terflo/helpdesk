package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserDTOFactory {

    @Autowired
    private UserServiceImpl userServiceImpl;

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
                user.getDescription(),
                user.getDate()
        );
    }

    //TODO: Проверять совпадания UserDTO и User
    public User convertToUser(UserDTO userDTO) {

        try {

            User user = userServiceImpl.findUserByUsername(userDTO.username);

            if(!Objects.equals(this.convertToUserDTO(user), userDTO))
                throw new UserNotFoundException("Пользователь не найден");

            return user;

        } catch (UserNotFoundException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Метод преобразует список User в список UserDTO
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
