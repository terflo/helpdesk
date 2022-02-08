package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.MessageService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер запросов пользователей на решение какой-либо проблемы
 */
@Controller
public class UserRequestsController {

    /**
     * Сервис для работы с пользователями
     */
    @Autowired
    UserService userService;

    @Autowired
    UserRequestService userRequestService;

    @Autowired
    MessageService messageService;

    /**
     * Контроллер GET-запроса на вывод страницы
     * @param authentication информация о пользователе
     * @param model переменные для отрисовки страницы
     * @throws AccessDeniedException возникает в случае ошибки обработки данных о пользователе
     * @return название страницы
     */
    @GetMapping("/requests")
    public String requests(Authentication authentication, Model model) throws AccessDeniedException {

        try {
            User user = userService.findUserByUsername(authentication.getName());
            List<UserRequest> userRequests = userRequestService.findUserRequestsByUser(user);
            model.addAttribute("name", user.getUsername());
            model.addAttribute("requests", userRequests);
        } catch (UserNotFoundException | UserRequestNotFoundException e) {
            throw new AccessDeniedException(e.getMessage());
        }
        return "requests";
    }

    /**
     * Контроллер GET-запроса на вывод страницы
     * @return название страницы
     */
    @GetMapping("/requests/add")
    public String requests_add() {
        return "add-request";
    }

    /**
     * Контроллер POST-запроса для создания запроса пользователя
     * @param authentication информация о пользователе
     * @param userRequest тело запроса пользователя (название, описание, приоритет и т.п.)
     * @param model переменные для отрисовки страницы
     * @return страница с запросами
     */
    @PostMapping("/requests/add")
    public String addRequest(Authentication authentication, UserRequest userRequest, Model model) {

        //Список ошибок
        ArrayList<String> errors = new ArrayList<>();

        //Проверка на пустое название проблемы
        if(userRequest.getName().isEmpty())
            errors.add("Название проблемы не может быть пустое");
        //Проверка на пустое описание проблемы
        if(userRequest.getDescription().isEmpty())
            errors.add("Описание проблемы не может быть пустое");
        //Проверка на существование пользователя, отправляющего запрос
        try {
            userRequest.setStatus(RequestStatus.BEGINNING); //устанавливаем статус что обращение принято в систему
            userRequest.setUser(userService.findUserByUsername(authentication.getName()));  //устанавливаем создателя обращения
        } catch (UserNotFoundException e) { //в случае если пользователь не был найден
            errors.add(e.getMessage());
        }

        if(errors.isEmpty()) {
            userRequestService.saveUserRequest(userRequest);
            return "redirect:/requests";
        } else {
            model.addAttribute("errors", errors);
            model.addAttribute("userRequest", userRequest); //возвращаем пользователю ту же страницу с введенными данными
            return "add-request";
        }
    }

    /**
     * Метод GET-запроса завершения запросов пользователя
     * @param id уникальный индификатор запроса
     * @return страница с запросами
     */
    @GetMapping("/requests/close/{id}")
    public String closeRequest(@PathVariable(value = "id") Long id) throws UserRequestNotFoundException {
        userRequestService.closeUserRequestByID(id);
        return "redirect:/requests";
    }

    /**
     * Метод GET-запроса просмотра запроса пользователя
     * @param id уникальный индификатор запроса
     * @param model переменные для отрисовки страницы
     * @return страница с запросом пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя
     */
    @GetMapping("/requests/{id}")
    public String showRequest(@PathVariable(value = "id") Long id, Model model) throws UserRequestNotFoundException {

        UserRequest userRequest = userRequestService.findUserRequestByID(id);

        model.addAttribute("userRequestID", userRequest);

        //Если сообщений в запросе пользователя не найдуется, то исключение проигнорируется и сообщения не будут выводится
        try {
            model.addAttribute("messages", messageService.findMessagesByUserRequest(userRequest));
        } catch (MessageNotFoundException ignored) {}

        return "request";
    }
}
