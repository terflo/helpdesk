package com.terflo.helpdesk.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factory.DecisionDTOFactory;
import com.terflo.helpdesk.model.factory.UserRequestDTOFactory;
import com.terflo.helpdesk.model.services.DecisionService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    private final UserService userService;

    private final DecisionService decisionService;

    private final DecisionDTOFactory decisionDTOFactory;

    private final UserRequestService userRequestService;

    private final UserRequestDTOFactory userRequestDTOFactory;

    /**
     * Mapping для вывода страницы админ-панели
     * @param model переменные для отрисовки страницы
     * @return страница админ-панели
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }

    /**
     * Mapping для вывода страницы с частыми вопросами
     * @param model переменные для отрисовки страницы
     * @return страница с частыми вопросами
     */
    @GetMapping("/admin/decisions")
    public String decisions(Model model) {
        model.addAttribute("decisions", decisionDTOFactory.convertToDecisionDTO(decisionService.findAllDecisions()));
        return "admin-decisions";
    }

    /**
     * Mapping для добавление частого вопроса
     * @param decision часто задаваемый вопрос
     * @param authentication авторизация клиента
     * @return HTTP ответ
     */
    @PostMapping("/admin/decisions/add")
    public @ResponseBody ResponseEntity<String> addDecision(@RequestBody Decision decision, Authentication authentication) {
        if(decision.getName().isEmpty() || decision.getAnswer().isEmpty()) {
            return ResponseEntity.badRequest().body("\"Поля не могут быть пустые\"");
        } else {
            decision.setDate(new Date());
            try {
                decision.setAuthor(userService.findUserByUsername(authentication.getName()));
                decision = decisionService.saveDecision(decision);
            } catch (DecisionNameAlreadyExistsException | UserNotFoundException e) {
                log.error(e.getMessage());
                return ResponseEntity.ok("\"" + e.getMessage() + "\"");
            }
        }
        log.info("Добавлен новый частый вопрос с индификатором " + decision.getId());


        try {
            return ResponseEntity.ok().body(decisionDTOFactory.convertToDecisionDTO(decision).toJSON());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok("\"" + e.getMessage() + "\"");
        }

    }

    /**
     * Mapping для удаления часто задаваемого вопроса
     * @param id уникальный индификато удаляемого элемента
     * @return HTTP ответ
     */
    @DeleteMapping("/admin/decisions/delete/{id}")
    public @ResponseBody ResponseEntity<String> deleteDecision(@PathVariable(name = "id") Long id) {
        try {
            decisionService.deleteDecision(id);
        } catch (DecisionNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        log.info("Удален частый вопрос id: " + id);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод для обновление данных о часто задаваемом вопросе
     * @param decision новые данные
     * @return HTTP ответ
     */
    @PostMapping("/admin/decisions/update")
    public @ResponseBody ResponseEntity<String> updateDecision(@RequestBody DecisionDTO decision) {
        try {
            decisionService.updateDecision(decision);
        } catch (DecisionNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
        log.info("Обновлена информация частого вопроса id: ");
        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * Mapping для вывода страницы с запросами пользователей
     * @param model переменные для отрисовки страницы
     * @return страница
     */
    @GetMapping("/admin/requests")
    public String requests(Model model) {
        List<UserRequest> requests = userRequestService.findAll();
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(requests));
        return "admin-requests";
    }
}
