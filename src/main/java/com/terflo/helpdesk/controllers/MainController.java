package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.factories.DecisionDTOFactory;
import com.terflo.helpdesk.model.services.DecisionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Контроллер для отображения содержимого на главной странице
 */
@Controller
@AllArgsConstructor
public class MainController {

    /**
     * Сервис частых вопросовв
     */
    private final DecisionService decisionService;

    /**
     * Фабрика частых вопросов
     */
    private final DecisionDTOFactory decisionDTOFactory;

    /**
     * Отображение главной страницы (index.html)
     * @param model переменные для отображения страницы
     * @return главная страница
     */
    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("decisions", decisionDTOFactory.convertToDecisionDTO(decisionService.findAllDecisions()));
        return "index";
    }
}
