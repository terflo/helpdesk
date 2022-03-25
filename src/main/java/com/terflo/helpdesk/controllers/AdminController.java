package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.factories.UserRequestDTOFactory;
import com.terflo.helpdesk.model.services.UserRequestServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.5
 * Контроллер админ-панели для управления системой
 */
@Log4j2
@Controller
@AllArgsConstructor
public class AdminController {

    /**
     * Сервис для работы обращений пользователей
     */
    private final UserRequestServiceImpl userRequestServiceImpl;

    /**
     * DTO фабрика обращений пользователей
     */
    private final UserRequestDTOFactory userRequestDTOFactory;

    /**
     * Mapping для вывода страницы админ-панели
     * @return страница админ-панели
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin/admin";
    }

    /**
     * Mapping для вывода страницы с запросами пользователей
     * @param model переменные для отрисовки страницы
     * @return страница
     */
    @GetMapping("/admin/requests")
    public String requests(Model model) {
        List<UserRequest> requests = userRequestServiceImpl.findAll();
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(requests));
        return "admin/requests";
    }

    /**
     * Mapping для вывода страницы с статистикой
     * @param model переменные для отрисовки страницы
     * @return страница
     */
    @GetMapping("/admin/statistics")
    public String statistics(Model model) {
        return "admin/statistics";
    }
}
