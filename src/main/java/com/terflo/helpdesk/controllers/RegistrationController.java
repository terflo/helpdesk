package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.requests.RegistrationRequest;
import com.terflo.helpdesk.model.responses.RegistrationResponse;
import com.terflo.helpdesk.model.services.CaptchaService;
import com.terflo.helpdesk.model.services.MailService;
import com.terflo.helpdesk.model.services.UserService;
import com.terflo.helpdesk.model.services.VerificationTokenService;
import com.terflo.helpdesk.utils.RegexUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Danil Krivoschiokov
 * @version 1.6
 * Контроллер страницы регистрации
 */
@Log4j2
@Controller
@AllArgsConstructor
public class RegistrationController {

    /**
     * Утилита для проверки строк через регулярные выражения
     */
    private final RegexUtil regexUtil;

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Сервис для работы с токенами валидации клиентов
     */
    private final VerificationTokenService verificationTokenService;

    /**
     * Почтовый сервис
     */
    private final MailService mailService;

    /**
     * Сервис каптчи (reCaptcha)
     */
    private final CaptchaService captchaService;

    /**
     * Контроллер страницы регистрации (метод GET)
     *
     * @return название страницы html
     */
    @GetMapping("/registration")
    public String registration() {
        return "user/registration";
    }

    @PostMapping("/registration")
    public String registerUser(@Validated RegistrationRequest request,
                               @RequestParam("g-recaptcha-response") String captchaResponse,
                               HttpServletRequest httpServletRequest,
                               Model model) {

        /* Список ошибок */
        ArrayList<String> errors = new ArrayList<>();

        try {
            captchaService.processResponse(captchaResponse, httpServletRequest.getRemoteAddr());
        } catch (InvalidReCaptchaException e) {
            log.error("[reCaptchaError] " + e.getMessage());
            errors.add(e.getMessage());
            model.addAttribute("data", request);
            model.addAttribute("errors", errors);
            return "user/registration";
        }

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
        if (!regexUtil.checkUsername(request.getUsername())) {
            errors.add("Некорректное имя пользователя");
        }

        if (errors.isEmpty()) {

            try {
                User user = userService.saveNewUser(request.getUsername(), request.getEmail(), request.getPassword());
                VerificationToken verificationToken = new VerificationToken(null, user, UUID.randomUUID().toString());
                verificationTokenService.saveToken(verificationToken);
                mailService.sendRegistrationMail(user.getEmail(), user.getUsername(), verificationToken.getActivateCode());
            } catch (UserAlreadyExistException | RoleNotFoundException | IOException | MessagingException e) {
                log.error(e.getMessage());
                model.addAttribute("status", 500);
                model.addAttribute("message", e.getMessage());
                model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
                return "error";
            }

            log.info("Зарегестрирован новый пользователь " + request.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("data", request);
            model.addAttribute("errors", errors);
            return "user/registration";
        }

    }

    @GetMapping("/activate/{username}/{activateCode}")
    public String activateUser(
            @PathVariable(name = "username") String username,
            @PathVariable(name = "activateCode") String activateCode,
            Model model) {

        log.info("Попытка активации пользователя " + username + " токеном верификации " + activateCode);

        try {
            VerificationToken verificationToken = verificationTokenService.findByActivateCode(activateCode);
            userService.activateUserByUsername(username);
            verificationTokenService.deleteToken(verificationToken);
        } catch (UserNotFoundException | UserAlreadyActivatedException | VerificationTokenNotFoundException e) {
            log.error(e.getMessage());
            model.addAttribute("status", 500);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
            return "error";
        }

        log.info("Пользователь " + username + " успешно активировал свой аккаунт");
        model.addAttribute("username", username);
        return "user/activate";
    }

    /**
     * Проверка существует ли заданное имя пользователя в базе данных
     * @param request запрос пользователя
     * @return результат верфикации данных
     */
    @ResponseBody
    @PostMapping("/registration/checkUserData")
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
