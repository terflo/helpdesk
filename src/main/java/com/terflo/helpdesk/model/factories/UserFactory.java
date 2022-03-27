package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.RoleService;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserFactory {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final ImageFactory imageFactory;

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

    public User convertToUser(UserDTO userDTO) {

        try {
            User user = userService.findUserByUsername(userDTO.username);

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


    @SneakyThrows
    public User buildNewUser(String username, String email, String password) {
        return new User(
                null,
                username,
                passwordEncoder.encode(password),
                email,
                new Date(),
                null,
                imageFactory.getImage(new File(ResourceUtils.getFile("classpath:static/img/user.png").getPath())),
                null,
                Collections.singleton(roleService.getRoleByName("ROLE_USER")),
                null,
                false,
                false,
                false,
                false
        );
    }

}
