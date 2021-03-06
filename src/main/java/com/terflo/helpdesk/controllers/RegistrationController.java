package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.VerificationToken;
import com.terflo.helpdesk.model.entity.enums.VerificationTypeToken;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.factories.UserFactory;
import com.terflo.helpdesk.model.requests.RegistrationRequest;
import com.terflo.helpdesk.model.services.CaptchaService;
import com.terflo.helpdesk.model.services.MailService;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import com.terflo.helpdesk.model.services.interfaces.VerificationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @author Danil Krivoschiokov
 * @version 1.8
 * Контроллер страницы регистрации
 */
@Log4j2
@Controller
@AllArgsConstructor
public class RegistrationController {

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
     * Фабрика пользователей
     */
    private final UserFactory userFactory;

    /**
     * Контроллер страницы регистрации (метод GET)
     * @return название страницы html
     */
    @GetMapping("/registration")
    public String registration() {
        return "user/registration";
    }

    /**
     * Регистрация нового пользователя
     * @param request запрос на регистрацию
     * @param captchaResponse результат reCaptcha
     * @param httpServletRequest HTTP запрос от клиента
     * @return HTTP результат регистрации
     */
    @ResponseBody
    @PostMapping("/registration")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegistrationRequest request,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            HttpServletRequest httpServletRequest) {

        //Валидация reCaptcha
        try {
            captchaService.processResponse(captchaResponse, httpServletRequest.getRemoteAddr());
        } catch (InvalidReCaptchaException e) {
            log.warn("[reCaptcha]" + e.getMessage());
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }

        //Сохранение пользователя в базе и создания токена подтверждения регистрации
        try {
            User user = userService.saveUser(userFactory.buildNewUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            ));
            VerificationToken verificationToken = VerificationToken.generateToken(user, VerificationTypeToken.REGISTRATION_CONFIRM);
            verificationTokenService.saveToken(verificationToken);
            mailService.sendRegistrationMail(user.getEmail(), user.getUsername(), verificationToken.getActivateCode());
        } catch (UserAlreadyExistException | MessagingException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }

        log.info("Зарегестрирован новый пользователь " + request.getUsername());
        return ResponseEntity.ok("\"ok\"");
    }

    /**
     * GET запрос активации аккаунта пользователя
     * @param username имя пользователя
     * @param activateCode код активации (токен)
     * @param model переменные для отрисовки страницы
     * @return страница с результатом активации
     */
    @GetMapping("/activate/{username}/{activateCode}")
    public String activateUser(
            @PathVariable(name = "username") String username,
            @PathVariable(name = "activateCode") String activateCode,
            Model model) {

        log.info("Попытка активации пользователя " + username + " токеном верификации " + activateCode);

        try {
            VerificationToken verificationToken = verificationTokenService.
                    findByActivateCodeAndUser(
                            activateCode,
                            userService.findUserByUsername(username),
                            VerificationTypeToken.REGISTRATION_CONFIRM
                    );
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
     * Метод проводит валидацию данных регистрации через аннотацию @Valid
     * @param request запрос верификации данных
     * @return HTTP ответ результат верификации
     */
    @ResponseBody
    @PostMapping("/registration/checkUserData")
    public ResponseEntity<String> checkRegistrationData2(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok("\"ok\"");
    }
}
