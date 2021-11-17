package com.terflo.helpdesk.controllers;


import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    UserService userService;

    /**
     * Проверка существует ли заданное имя пользователя в базе данных
     * TODO: Переделать на метод Post
     * @param username имя пользователя
     * @return результат поиска (true/false)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/usernameIsExist/{username}")
    public boolean usernameIsExist(@PathVariable("username") String username) {
        return userService.userIsExistByUsername(username);
    }

}
