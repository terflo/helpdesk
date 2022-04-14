package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factories.ImageFactory;
import com.terflo.helpdesk.model.factories.UserFactory;
import com.terflo.helpdesk.model.factories.UserRequestFactory;
import com.terflo.helpdesk.model.services.SessionService;
import com.terflo.helpdesk.model.services.interfaces.RoleService;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import com.terflo.helpdesk.utils.RegexUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с пользователями
 * @author Danil Krivoschiokov
 * @version 1.4
 */
@Log4j2
@Controller
@AllArgsConstructor
public class UserController {

    private final SessionService sessionService;

    private final UserService userService;

    private final UserFactory userFactory;

    private final ImageFactory imageFactory;

    private final RoleService roleService;

    private final UserRequestService userRequestService;

    private final UserRequestFactory userRequestFactory;


    /**
     * Отображение профиля пользователя
     * @param username уникальный ник пользователя
     * @param model переменные для отображения страницы
     * @return страница профиля пользователя
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username, Model model, Authentication authentication) {

        User user;
        try {
            user = userService.findUserByUsername(username);
        } catch (UserNotFoundException e) {
            log.error("Попытка получения профиля несуществующего пользователя " + username);
            model.addAttribute("status", 404);
            model.addAttribute("message", e.getMessage());
            return "error";
        }

        boolean clientIsContainsAdminRole = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()).contains("ROLE_ADMIN");

        List<String> clientRoles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        model.addAttribute("clientRoles", clientRoles);
        model.addAttribute("clientUsername", authentication.getName());
        model.addAttribute("isAdmin", clientIsContainsAdminRole);

        List<UserRequest> requests = userRequestService.findAllUserRequestsByUser(user);
        model.addAttribute("requests", userRequestFactory.convertToUserRequestDTO(requests));

        model.addAttribute("avatar", user.getAvatar());
        model.addAttribute("user", userFactory.convertToUserDTO(user));
        return "user/profile";

    }


    /**
     * POST запрос обновления аватара пользователя
     * @param id уникальый индификатор пользователя
     * @param file файл с аватаркой
     * @return HTTP ответ
     */
    @ResponseBody
    @PutMapping("/user/{id}/avatar")
    public ResponseEntity<String> updateUserAvatar(@PathVariable(name = "id") Long id, @RequestParam MultipartFile file) {

        try {

            User user = userService.findUserById(id);
            user.setAvatar(imageFactory.getImage(file));
            userService.updateUser(user);

        } catch (UserNotFoundException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("\"\"");
    }


    /**
     * POST запрос на получение аватарки пользователя в Base64 кодировке
     * @param id уникальный индификатор пользователя
     * @return изображение в Base64 кодировке
     */
    @ResponseBody
    @GetMapping("/user/{id}/avatar")
    public ResponseEntity<String> getAvatar(@PathVariable(name = "id") Long id) {

        try {
            Image image = userService.findUserById(id).getAvatar();
            return ResponseEntity.ok("\"" + image.getBase64ImageWithType() + "\"");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * POST запрос на обновлении информации о пользователе
     * @param id уникальный индификатор пользователя
     * @param userDTO новая информация о пользователе
     * @return HTTP ответ
     */
    @ResponseBody
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") Long id,
                                             @RequestBody UserDTO userDTO,
                                             Authentication authentication) {

        try {

            boolean needLogout = false;
            User user = userService.findUserById(id);

            //Проверка username на изменения
            if(!userDTO.username.equals(user.getUsername())) {
                user.setUsername(userDTO.username);
                needLogout = true;
            }

            //Проверка email на изменения
            if(!userDTO.email.equals(user.getEmail())) {
                user.setEmail(userDTO.email);
            }

            //Проверка description на изменения
            if(!userDTO.description.equals(user.getDescription()))
                user.setDescription(userDTO.description);

            userService.updateUser(user);

            if (needLogout) sessionService.expireUserSessions(authentication.getName());
            return ResponseEntity.ok().body("\"\"");

        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Mapping для вывода списка всех пользователей
     * @param model переменные для отрисовки страницы
     * @return страница с списком пользователей
     */
    @GetMapping("/admin/users")
    public String getUsers(Model model) {

        model.addAttribute("roleNames", roleService
                .findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeUsers", userService.getActiveUsernamesFromSessionRegistry());

        return "admin/users";
    }


    /**
     * Mapping для удаления пользователя из базы методом post
     * @param id уникальный индификатор пользователя
     * @return возврат на страницу с списком пользователей
     */
    @ResponseBody
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) {
        try {
            userService.deleteUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Mapping post запроса для переключения блокировки пользователя
     * @param id уникальный индификатор пользователя
     * @return HTTP ответ
     */
    @ResponseBody
    @PutMapping("/admin/users/{id}/switchLock")
    public ResponseEntity<String> switchLockUser(@PathVariable(value = "id") Long id) {
        try {
            userService.switchLockUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        sessionService.expireUserSessions(id);
        return ResponseEntity.ok().body("\"\"");
    }


    @ResponseBody
    @PutMapping("/users/{id}/{roleName}")
    public ResponseEntity<String> addUserRole(@PathVariable("id") Long id, @PathVariable("roleName") String roleName) {

        try {
            Role role = roleService.getRoleByName(roleName);
            userService.addRoleToUser(id, role);
        } catch (RoleNotFoundException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }

        return ResponseEntity.ok().body("\"\"");
    }


    @ResponseBody
    @DeleteMapping("/users/{id}/{roleName}")
    public ResponseEntity<String> deleteUserRole(@PathVariable("id") Long id,
                                                 @PathVariable("roleName") String roleName) {

        try {
            Role role = roleService.getRoleByName(roleName);
            userService.deleteRoleToUser(id, role);
        } catch (RoleNotFoundException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }

        return ResponseEntity.ok().body("\"\"");
    }


    //TODO: Написать логику восстановления пароля
    @ResponseBody
    @PostMapping("users/restorePassword")
    public ResponseEntity<String> restorePassword(@RequestBody String email) {

        return ResponseEntity.ok().body("\"\"");
    }
}
