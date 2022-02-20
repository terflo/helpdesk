package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.UserDTO;
import com.terflo.helpdesk.model.exceptions.ImageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factory.UserDTOFactory;
import com.terflo.helpdesk.model.services.ImageService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Контроллер для работы с пользователями
 * @author Danil Krivoschiokov
 * @version 1.0
 */
@Controller
public class UserController {

    private final UserService userService;

    private final UserDTOFactory userDTOFactory;

    private final ImageService imageService;

    public UserController(UserService userService, UserDTOFactory userDTOFactory, ImageService imageService) {
        this.userService = userService;
        this.userDTOFactory = userDTOFactory;
        this.imageService = imageService;
    }

    /**
     * Отображение профиля пользователя
     * @param username уникальный ник пользователя
     * @param model переменные для отображения страницы
     * @return страница профиля пользователя
     * @throws UserNotFoundException возникает в случае ненахождении пользователя в базе данных
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username, Model model) throws UserNotFoundException {

        User user = userService.findUserByUsername(username);
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
    @PostMapping("/user/{id}/updateAvatar")
    public ResponseEntity<String> updateUserAvatar(@PathVariable(name = "id") Long id, @RequestParam MultipartFile file) {

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
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }
        return ResponseEntity.ok("\"OK\"");
    }

    /**
     * POST запрос на получение аватарки пользователя в Base64 кодировке
     * @param id уникальный индификатор пользователя
     * @return изображение в Base64 кодировке
     */
    @PostMapping("/user/{id}/getAvatar")
    public ResponseEntity<String> getAvatar(@PathVariable(name = "id") Long id) {

        try {
            User user = userService.findUserById(id);
            Image image = imageService.getImage(user.getAvatar_id());
            return ResponseEntity.ok("\"data:image/png;base64," + image.getBase64Image() + "\"");
        } catch (UserNotFoundException | ImageNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/update")
    public ResponseEntity<String> updateUser(UserDTO userDTO) {

        try {
            //TODO: Дописать
            User user = userService.findUserByUsername(userDTO.username);

        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("\"OK\"");
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
     * @param model переменные для отрисовки страницы
     * @return возврат на страницу с списком пользователей
     */
    @DeleteMapping("/admin/users/delete/{id}")
    public @ResponseBody
    ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id, Model model) {
        try {
            userService.deleteUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
    }
}
