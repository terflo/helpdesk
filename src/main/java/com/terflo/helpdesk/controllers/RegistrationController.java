package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import com.terflo.helpdesk.model.requests.RegistrationRequest;
import com.terflo.helpdesk.model.services.UserService;
import com.terflo.helpdesk.utils.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер страницы регистрации
 */
@Controller
public class RegistrationController {

    /**
     * Утилита для проверки строк через регулярные выражения
     */
    @Autowired
    RegexUtil regexUtil;

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
    public String registerUser(RegistrationRequest request, Model model) {

        ArrayList<String> errors = new ArrayList<>();

        if(!request.getPassword().equals(request.getPasswordConfirm())) {
            errors.add("Пароли не совпадают");
        }

        if(!regexUtil.checkEmail(request.getEmail())) {
            errors.add("Некорректный email");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());

        if(request.getUsername().length() >= 4) {
            try {
                userService.saveUser(user);
            } catch (UserAlreadyExistException userAlreadyExistsException) {
                errors.add(userAlreadyExistsException.getMessage());
            }
        } else {
            errors.add("Слишком короткое имя");
        }

        if(errors.isEmpty())
            return "redirect:/login";
        else {
            model.addAttribute("errors", errors);
            return "/registration";
        }
    }
}
