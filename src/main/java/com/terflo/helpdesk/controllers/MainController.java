package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.factory.DecisionDTOFactory;
import com.terflo.helpdesk.model.services.DecisionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер для отображения содержимого на главной странице
 */
@Controller
public class MainController {

    private final DecisionService decisionService;

    private final DecisionDTOFactory decisionDTOFactory;

    public MainController(DecisionService decisionService, DecisionDTOFactory decisionDTOFactory) {
        this.decisionService = decisionService;
        this.decisionDTOFactory = decisionDTOFactory;
    }

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
