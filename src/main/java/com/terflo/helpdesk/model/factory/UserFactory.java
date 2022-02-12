package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserFactory {

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
                        .collect(Collectors.toSet())
        );
    }

}
