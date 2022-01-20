package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.requests.RegistrationRequest;
import com.terflo.helpdesk.model.responses.RegistrationResponse;
import com.terflo.helpdesk.model.services.UserService;
import com.terflo.helpdesk.utils.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
 * REST контроллер сервера
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    RegexUtil regexUtil;

    @Autowired
    UserService userService;

    /**
     * Проверка существует ли заданное имя пользователя в базе данных
     * @param request запрос пользователя
     * @return результат верфикации данных
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/checkRegistrationData")
    @ResponseBody
    public RegistrationResponse checkRegistrationData(@RequestBody RegistrationRequest request) {

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

        return response;
    }

}
