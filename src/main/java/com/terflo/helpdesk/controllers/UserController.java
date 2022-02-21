package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.ImageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factory.UserDTOFactory;
import com.terflo.helpdesk.model.factory.UserRequestDTOFactory;
import com.terflo.helpdesk.model.services.ImageService;
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

    private final UserService userService;

    private final UserDTOFactory userDTOFactory;

    private final ImageService imageService;

    private final UserRequestService userRequestService;

    private final UserRequestDTOFactory userRequestDTOFactory;

    public UserController(RegexUtil regexUtil, UserService userService, UserDTOFactory userDTOFactory, ImageService imageService, UserRequestService userRequestService, UserRequestDTOFactory userRequestDTOFactory) {
        this.regexUtil = regexUtil;
        this.userService = userService;
        this.userDTOFactory = userDTOFactory;
        this.imageService = imageService;
        this.userRequestService = userRequestService;
        this.userRequestDTOFactory = userRequestDTOFactory;
    }

    /**
     * Отображение профиля пользователя
     * @param username уникальный ник пользователя
     * @param model переменные для отображения страницы
     * @return страница профиля пользователя
     * @throws UserNotFoundException возникает в случае ненахождении пользователя в базе данных
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username, Model model, Authentication authentication) throws UserNotFoundException {


        boolean clientIsContainsAdminRole = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()).contains("ROLE_ADMIN");

        List<String> clientRoles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        model.addAttribute("clientRoles", clientRoles);
        model.addAttribute("clientUsername", authentication.getName());
        model.addAttribute("isAdmin", clientIsContainsAdminRole);

        User user = userService.findUserByUsername(username);
        List<UserRequest> requests = userRequestService.findAllUserRequestsByUser(user);
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(requests));

        try {
            model.addAttribute("avatar", imageService.getImage(user.getAvatar_id()));
        } catch (ImageNotFoundException e) {
            model.addAttribute("avatar", null);
        }
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
            if (user.getAvatar_id() == null) {
                Long imageId = imageService.saveImage(file);
                user.setAvatar_id(imageId);
            } else {
                imageService.updateImage(user.getAvatar_id(), file);
            }
            userService.updateUser(user);
        } catch (UserNotFoundException | IOException | ImageNotFoundException e) {
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
            User user = userService.findUserById(id);
            Image image = imageService.getImage(user.getAvatar_id());
            return ResponseEntity.ok("\"data:" + image.getType() + ";base64," + image.getBase64Image() + "\"");
        } catch (UserNotFoundException | ImageNotFoundException e) {
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
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") Long id, @RequestBody UserDTO userDTO) {

        try {
            User user = userService.findUserById(id);

            //Проверка username на изменения
            if(!userDTO.username.equals(user.getUsername())) {
                //Проверка username на уникальность
                if (userService.userIsExistByUsername(userDTO.username))
                    return ResponseEntity.badRequest().body("Такое имя уже занято");
                //Проверка username на корректность (Regex)
                if (!regexUtil.checkUsername(userDTO.username))
                    return ResponseEntity.badRequest().body("Некорректное имя пользователя");
            }

            //Проверка email на изменения
            if(!userDTO.email.equals(user.getEmail())) {
                //Проверка email на уникальность
                if (userService.userIsExistByEmail(userDTO.email))
                    return ResponseEntity.badRequest().body("Такой email уже занят");
                //Проверка email на корректность (Regex)
                if (!regexUtil.checkEmail(userDTO.email))
                    return ResponseEntity.badRequest().body("Некорректный email");
            }

            //Проверка description на изменения
            if(!userDTO.description.equals(user.getDescription()))
                user.setDescription(userDTO.description);

            userService.updateUser(user);

        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

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
        } catch (UserNotFoundException | ImageNotFoundException e) {
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
