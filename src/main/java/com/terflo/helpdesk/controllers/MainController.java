package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factory.DecisionDTOFactory;
import com.terflo.helpdesk.model.repositories.RoleRepository;
import com.terflo.helpdesk.model.services.DecisionService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileNotFoundException;

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
    public String main(Model model) throws UserNotFoundException {
        model.addAttribute("decisions", decisionDTOFactory.convertToDecisionDTO(decisionService.findAllDecisions()));
        return "index";
    }
}
