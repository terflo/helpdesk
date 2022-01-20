package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер админ-панели для управления системой
 */
@Controller
public class AdminController {

    @Autowired
    UserService userService;

    /**
     * Mapping для вывода страницы админ-панели
     * @param model
     * @return
     */
    @GetMapping("/admin/")
    public String admin(Model model) {
        return "admin";
    }

    /**
     * Mapping для вывода списка всех пользователей
     * @param model
     * @return
     */
    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    /**
     * Mapping для вывода конкретной информации по пользователю
     * @param id уникальный индификатор пользователя
     * @param model
     * @return
     */
    @GetMapping("/admin/users/{id}")
    public String getUserDetail(@PathVariable(value = "id") Long id, Model model) {
        try {
            model.addAttribute("user", userService.findUserById(id));
        } catch (UserNotFoundException e) {
            model.addAttribute("messageIsExist", true);
            model.addAttribute("message", e.getMessage());
        }
        return "userDetail";
    }

    /**
     * Mapping для удаления пользователя из базы методом post
     * @param id уникальный индификатор пользователя
     * @param model
     * @return
     */
    @PostMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable(value = "id") Long id, Model model) {
        try {
            userService.deleteUserById(id);
        } catch (UserNotFoundException e) {
            model.addAttribute("messageIsExist", true);
            model.addAttribute("message", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
