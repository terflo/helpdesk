package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Image;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import com.terflo.helpdesk.model.factory.ImageFactory;
import com.terflo.helpdesk.model.requests.RegistrationRequest;
import com.terflo.helpdesk.model.responses.RegistrationResponse;
import com.terflo.helpdesk.model.services.UserService;
import com.terflo.helpdesk.utils.RegexUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * Контроллер страницы регистрации
 */
@Log4j2
@Controller
public class RegistrationController {

    /**
     * Утилита для проверки строк через регулярные выражения
     */
    private final RegexUtil regexUtil;

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    private final ImageFactory imageFactory;

    public RegistrationController(RegexUtil regexUtil, UserService userService, ImageFactory imageFactory) {
        this.regexUtil = regexUtil;
        this.userService = userService;
        this.imageFactory = imageFactory;
    }

    /**
     * Контроллер страницы регистрации (метод GET)
     *
     * @return название страницы html
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(RegistrationRequest request, Model model) {

        /* Список ошибок */
        ArrayList<String> errors = new ArrayList<>();

        if(request.getPassword().trim().isEmpty() || request.getUsername().trim().isEmpty()) {
            errors.add("Заполните все поля");
        } else
        /* Работа с паролем */
        if (request.getPassword().length() < 5) {
            errors.add("Пароль слишком короткий");
        } else if (!request.getPassword().equals(request.getPasswordConfirm())) {
            errors.add("Пароли не совпадают");
        }

        /* Работа с email */
        if (!regexUtil.checkEmail(request.getEmail())) {
            errors.add("Некорректный email");
        } else if (userService.userIsExistByEmail(request.getEmail())) {
            errors.add("Данный email уже зарегестрирован");
        }

        /* Работа с username */
        if (regexUtil.checkUsername(request.getUsername())) {

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());

            try {
                Image image = imageFactory.getImage(new File(ResourceUtils.getFile("classpath:static/img/user.png").getPath()));
                user.setAvatar(image);
            } catch (IOException e) {
                errors.add(e.getMessage());
            }


            try {
                userService.saveNewUser(user);
            } catch (UserAlreadyExistException | RoleNotFoundException userAlreadyExistsException) {
                errors.add(userAlreadyExistsException.getMessage());
            }

        } else {
            errors.add("Некорректное имя пользователя");
        }

        if (errors.isEmpty()) {
            log.info("Зарегестрирован новый пользователь " + request.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("data", request);
            model.addAttribute("errors", errors);
            return "/registration";
        }
    }

    /**
     * Проверка существует ли заданное имя пользователя в базе данных
     * @param request запрос пользователя
     * @return результат верфикации данных
     */
    @RequestMapping(method = RequestMethod.POST, value = "/registration/checkUserData")
    @ResponseBody
    public ResponseEntity<RegistrationResponse> checkRegistrationData(@RequestBody RegistrationRequest request) {

        RegistrationResponse response = new RegistrationResponse();

        //Работа с именем пользователя
        if(!request.getUsername().isEmpty()) {

            String username = request.getUsername();

            if (!regexUtil.checkUsername(username)) {
                response.setUsernameStatus("INCORRECT USERNAME");
            } else if (userService.userIsExistByUsername(username)) {
                response.setUsernameStatus("ALREADY EXISTS");
            } else {
                response.setUsernameStatus("OK");
            }
        }

        //Работа с email пользователя
        if(!request.getEmail().isEmpty()) {

            String email = request.getEmail();

            if(userService.userIsExistByEmail(email)) {
                response.setEmailStatus("ALREADY EXISTS");
            } else if (!regexUtil.checkEmail(email)) {
                response.setEmailStatus("INCORRECT EMAIL");
            } else {
                response.setEmailStatus("OK");
            }
        }

        //Работа с паролем пользователя
        if(!request.getPassword().isEmpty() && !request.getPasswordConfirm().isEmpty()) {

            String password = request.getPassword();
            String confirm = request.getPasswordConfirm();

            if (password.length() < 5) {
                response.setPasswordStatus("TOO SHORT");
            } else if (password.equals(confirm)) {
                response.setPasswordStatus("OK");
            } else {
                response.setPasswordStatus("NOT MATCH");
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
