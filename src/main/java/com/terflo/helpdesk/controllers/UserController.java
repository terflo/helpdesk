package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factory.ImageFactory;
import com.terflo.helpdesk.model.factory.UserDTOFactory;
import com.terflo.helpdesk.model.factory.UserRequestDTOFactory;
import com.terflo.helpdesk.model.services.SessionService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import com.terflo.helpdesk.utils.RegexUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с пользователями
 * @author Danil Krivoschiokov
 * @version 1.1
 */
@Controller
public class UserController {

    private final static List<String> allowedContent = Arrays.asList("image/png", "image/jpeg", "image/jpg");

    private final RegexUtil regexUtil;

    private final SessionService sessionService;

    private final UserService userService;

    private final UserDTOFactory userDTOFactory;

    private final ImageFactory imageFactory;

    private final UserRequestService userRequestService;

    private final UserRequestDTOFactory userRequestDTOFactory;

    public UserController(RegexUtil regexUtil, SessionService sessionService, UserService userService, UserDTOFactory userDTOFactory, ImageFactory imageFactory, UserRequestService userRequestService, UserRequestDTOFactory userRequestDTOFactory) {
        this.regexUtil = regexUtil;
        this.sessionService = sessionService;
        this.userService = userService;
        this.userDTOFactory = userDTOFactory;
        this.imageFactory = imageFactory;
        this.userRequestService = userRequestService;
        this.userRequestDTOFactory = userRequestDTOFactory;
    }

    /**
     * Отображение профиля пользователя
     * @param username уникальный ник пользователя
     * @param model переменные для отображения страницы
     * @return страница профиля пользователя
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username, Model model, Authentication authentication) throws UserNotFoundException {

        User user = null;
        user = userService.findUserByUsername(username);
        /*try {
            user = userService.findUserByUsername(username);
        } catch (UserNotFoundException e) {
            model.addAttribute("statusCode", "404 Не найдено");
            model.addAttribute("exception", e);
            return "error";
        }*/

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
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(requests));

        model.addAttribute("avatar", user.getAvatar());
        model.addAttribute("user", userDTOFactory.convertToUserDTO(user));
        return "profile";

    }

    /**
     * POST запрос обновления аватара пользователя
     * @param id уникальый индификатор пользователя
     * @param file файл с аватаркой
     * @return HTTP ответ
     */
    @ResponseBody
    @PostMapping("/user/{id}/updateAvatar")
    public ResponseEntity<String> updateUserAvatar(@PathVariable(name = "id") Long id, @RequestParam MultipartFile file) {

        if(!allowedContent.contains(file.getContentType()))
            return ResponseEntity.badRequest().body("Не поддерживаемый тип изображения");

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
    @PostMapping("/user/{id}/getAvatar")
    public ResponseEntity<String> getAvatar(@PathVariable(name = "id") Long id) {

        try {
            Image image = userService.findUserById(id).getAvatar();
            return ResponseEntity.ok("\"data:" + image.getType() + ";base64," + image.getBase64Image() + "\"");
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
    @PostMapping("/user/{id}/update")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") Long id, @RequestBody UserDTO userDTO, Authentication authentication) {

        boolean needLogout = false;
        User user;

        try {
            user = userService.findUserById(id);

            //Проверка username на изменения
            if(!userDTO.username.equals(user.getUsername())) {
                //Проверка username на уникальность
                if (userService.userIsExistByUsername(userDTO.username))
                    return ResponseEntity.badRequest().body("Такое имя уже занято");
                //Проверка username на корректность (Regex)
                if (!regexUtil.checkUsername(userDTO.username))
                    return ResponseEntity.badRequest().body("Некорректное имя пользователя");
                user.setUsername(userDTO.username);
                needLogout = true;
            }

            //Проверка email на изменения
            if(!userDTO.email.equals(user.getEmail())) {
                //Проверка email на уникальность
                if (userService.userIsExistByEmail(userDTO.email))
                    return ResponseEntity.badRequest().body("Такой email уже занят");
                //Проверка email на корректность (Regex)
                if (!regexUtil.checkEmail(userDTO.email))
                    return ResponseEntity.badRequest().body("Некорректный email");
                user.setEmail(userDTO.email);
            }

            //Проверка description на изменения
            if(!userDTO.description.equals(user.getDescription()))
                user.setDescription(userDTO.description);

            userService.updateUser(user);

        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        if(needLogout) sessionService.expireUserSessions(authentication.getName());
        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * Mapping для вывода списка всех пользователей
     * @param model переменные для отрисовки страницы
     * @return страница с списком пользователей
     */
    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeUsers", userService.getActiveUsernamesFromSessionRegistry());
        return "admin-users";
    }

    /**
     * Mapping для удаления пользователя из базы методом post
     * @param id уникальный индификатор пользователя
     * @return возврат на страницу с списком пользователей
     */
    @ResponseBody
    @DeleteMapping("/admin/users/delete/{id}")
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
    @PostMapping("/admin/users/switchLock/{id}")
    public ResponseEntity<String> switchLockUser(@PathVariable(value = "id") Long id) {
        try {
            userService.switchLockUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("\"\"");
    }
}
