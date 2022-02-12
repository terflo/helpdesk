package com.terflo.helpdesk.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер для отображения содержимого на главной странице
 */
@Controller
public class MainController {

    /**
     * Отображение главной страницы (index.html)
     * @return главная страница
     */
    @GetMapping("/")
    public String main() {
        return "index";
    }
}
