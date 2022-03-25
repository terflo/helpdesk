package com.terflo.helpdesk.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.factories.DecisionFactory;
import com.terflo.helpdesk.model.services.DecisionServiceImpl;
import com.terflo.helpdesk.model.services.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * Контроллер для работы с часто задаваемыми вопросами
 * @version 1.2
 * @author Danil Krivoschiokov
 */
@Log4j2
@Controller
@AllArgsConstructor
public class DecisionController {

    /**
     * Серввис для работы с пользователями
     */
    private final UserServiceImpl userServiceImpl;

    /**
     * Сервис для работы с частыми вопросами
     */
    private final DecisionServiceImpl decisionServiceImpl;

    /**
     * DTO фабрика частых вопросов
     */
    private final DecisionFactory decisionFactory;

    /**
     * Mapping для вывода страницы с частыми вопросами
     * @param model переменные для отрисовки страницы
     * @return страница с частыми вопросами
     */
    @GetMapping("/decisions")
    public String decisions(Model model) {
        model.addAttribute("decisions", decisionFactory.convertToDecisionDTO(decisionServiceImpl.findAllDecisions()));
        return "admin/decisions";
    }

    /**
     * Добавление часто задаваемого вопроса в базу данных
     * @param decision часто задаваемый вопрос
     * @param authentication данные авторизации пользователя
     * @return сохраненный часто задаваемый вопрос
     */
    @ResponseBody
    @PostMapping("/decisions")
    public ResponseEntity<String> addDecision(@Valid @RequestBody DecisionDTO decision, Authentication authentication) {

        try {
            Decision newDecision = decisionFactory.convertToDecision(decision);
            newDecision.setAuthor(userServiceImpl.findUserByUsername(authentication.getName()));
            newDecision.setDate(new Date());
            newDecision = decisionServiceImpl.saveDecision(newDecision);

            log.info("Добавлен новый частый вопрос #" + newDecision.getId());
            return ResponseEntity.ok().body(decisionFactory.convertToDecisionDTO(newDecision).toJSON());

        } catch (UserNotFoundException | DecisionNameAlreadyExistsException | JsonProcessingException e) {
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }
    }

    /**
     * Mapping для удаления часто задаваемого вопроса
     * @param id уникальный индификато удаляемого элемента
     * @return HTTP ответ
     */
    @ResponseBody
    @DeleteMapping("/decisions/{id}")
    public ResponseEntity<String> deleteDecision(@PathVariable(name = "id") Long id) {
        try {
            decisionServiceImpl.deleteDecisionByID(id);
        } catch (DecisionNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        log.info("Удален частый вопрос #" + id);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод для обновление данных о часто задаваемом вопросе
     * @param decision новые данные
     * @return HTTP ответ
     */
    @ResponseBody
    @PutMapping("/decisions")
    public ResponseEntity<String> updateDecision(@Valid @RequestBody DecisionDTO decision) {

        try {
            Decision updatedDecision = decisionFactory.convertToDecision(decision);
            decisionServiceImpl.updateDecision(updatedDecision);
        } catch (DecisionNotFoundException | UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok("\"" + e.getMessage() + "\"");
        }

        log.info("Обновлена информация частого вопроса #" + decision.id);
        return ResponseEntity.ok().body("\"\"");
    }
}
