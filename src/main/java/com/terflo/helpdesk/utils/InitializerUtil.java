package com.terflo.helpdesk.utils;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.RoleAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factories.ImageFactory;
import com.terflo.helpdesk.model.services.RoleServiceImpl;
import com.terflo.helpdesk.model.services.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class InitializerUtil {

    private final static List<String> requiredRoles = Arrays.asList("ROLE_USER", "ROLE_OPERATOR", "ROLE_ADMIN");

    private final RoleServiceImpl roleServiceImpl;

    private final UserServiceImpl userServiceImpl;

    private final ImageFactory imageFactory;

    private final BCryptPasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException {

        //Проверка на существование обязательных ролей
        for(String roleName : requiredRoles) {
            try {
                roleServiceImpl.getRoleByName(roleName);
            } catch (RoleNotFoundException e) {
                try {
                    log.warn("Роль с именем " + roleName + " не найдена в базе данных и будет перезаписана");
                    roleServiceImpl.saveRole(new Role(null, roleName, null));
                    //Игнорим исключение, так как выше проверяет существует ли в базе роль с таким именем
                } catch (RoleAlreadyExistException ignored) {}
            }
        }

        User user;
        //Проверка на существование пользователя root
        try {
            user = userServiceImpl.findUserByUsername("root");
        } catch (UserNotFoundException e) {
            log.warn("Пользователь root не найден в базе и будет перезаписан");
            User root = new User();
            root.setUsername("root");
            root.setPassword(passwordEncoder.encode("root"));
            root.setEmail("root@example.com");
            root.setEnabled(true);
            root.setCredentials_expired(false);
            root.setExpired(false);
            root.setLocked(false);
            root.setAvatar(imageFactory.getImage(getClass().getResourceAsStream("/static/img/user.png"), "image/png"));
            root.setRoles(roleServiceImpl.findAll());
            root.setDate(new Date());
            try {
                userServiceImpl.saveUser(root);
            } catch (UserAlreadyExistException ignored) {}
            return;
        }

        //Последний этап проверки: проверка есть ли у пользователя root все возможные права
        if(user.getRoles().size() != roleServiceImpl.findAll().size()) {
            log.warn("У пользователя root не обнаружен полный список прав, права будут установленны автоматически");
            user.setRoles(roleServiceImpl.findAll());
            try {
                userServiceImpl.updateUser(user);
            } catch (UserNotFoundException ignored) {}
        }
    }
}