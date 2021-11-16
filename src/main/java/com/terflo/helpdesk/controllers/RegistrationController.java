package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер страницы регистрации
 */
@Controller
public class RegistrationController {

    /**
     * Сервис для работы с пользователями
     */
    @Autowired
    private UserService userService;

    /**
     * Контроллер страницы регистрации (метод GET)
     * @return название страницы html
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(User user, Map<String, Object> model) {

        try {
            userService.saveUser(user);
        } catch (UserAlreadyExistException userAlreadyExistsException) {
            model.put("message", userAlreadyExistsException.getMessage());
            return "/registration";
        }

        return "redirect:/login";
    }

}
