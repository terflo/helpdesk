package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Decision;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.DecisionDTO;
import com.terflo.helpdesk.model.exceptions.DecisionNameAlreadyExistsException;
import com.terflo.helpdesk.model.exceptions.DecisionNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.factory.DecisionFactory;
import com.terflo.helpdesk.model.factory.UserRequestFactory;
import com.terflo.helpdesk.model.services.DecisionService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер админ-панели для управления системой
 */
@Controller
public class AdminController {

    private final UserService userService;

    private final DecisionService decisionService;

    private final DecisionFactory decisionFactory;

    private final UserRequestService userRequestService;

    private final UserRequestFactory userRequestFactory;

    public AdminController(UserService userService, DecisionService decisionService, DecisionFactory decisionFactory, UserRequestService userRequestService, UserRequestFactory userRequestFactory) {
        this.userService = userService;
        this.decisionService = decisionService;
        this.decisionFactory = decisionFactory;
        this.userRequestService = userRequestService;
        this.userRequestFactory = userRequestFactory;
    }

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
        model.addAttribute("decisions", decisionFactory.convertToDecisionDTO(decisionService.findAllDecisions()));
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
            return ResponseEntity.ok("\"Поля не могут быть пустые\"");
        } else {
            decision.setDate(new Date());
            try {
                decision.setAuthor(userService.findUserByUsername(authentication.getName()));
                decisionService.saveDecision(decision);
            } catch (DecisionNameAlreadyExistsException | UserNotFoundException e) {
                return ResponseEntity.ok("\"" + e.getMessage() + "\"");
            }
        }
        return ResponseEntity.ok("\"OK\"");
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
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
            return ResponseEntity.ok(e.getMessage());
        }
        return ResponseEntity.ok("\"OK\"");
    }

    /**
     * Mapping для вывода страницы с запросами пользователей
     * @param model переменные для отрисовки страницы
     * @return страница
     */
    @GetMapping("/admin/requests")
    public String requests(Model model) {
        List<UserRequest> requests = userRequestService.findAll();
        model.addAttribute("requests", userRequestFactory.convertToUserRequestDTO(requests));
        return "admin-requests";
    }

    /**
     * POST запрос на удаление обращения пользователя
     * @param id уникальный индификатор обращения
     * @return страница с обращениями
     */
    @DeleteMapping("/admin/requests/delete/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable(value = "id") Long id) {
        try {
            userRequestService.deleteByID(id);
        } catch (UserRequestNotFoundException e) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
    }

    /**
     * Mapping для вывода списка всех пользователей
     * @param model переменные для отрисовки страницы
     * @return страница с списком пользователей
     */
    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeUsers", userService.getActiveUsernamesFromSessionRegistry());
        return "admin-users";
    }

    /**
     * Mapping для вывода конкретной информации по пользователю
     * @param id уникальный индификатор пользователя
     * @param model переменные для отрисовки страницы
     * @return страница с информацией по конкретному пользователю
     */
    @GetMapping("/admin/users/{id}")
    public String getUserDetail(@PathVariable(value = "id") Long id, Model model){
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
     * @param model переменные для отрисовки страницы
     * @return возврат на страницу с списком пользователей
     */
    @DeleteMapping("/admin/users/delete/{id}")
    public @ResponseBody ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id, Model model) {
        try {
            userService.deleteUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
    }

    /**
     * Mapping post запроса для переключения блокировки пользователя
     * @param id уникальный индификатор пользователя
     * @return HTTP ответ
     */
    @PostMapping("/admin/users/switchLock/{id}")
    public ResponseEntity<String> switchLockUser(@PathVariable(value = "id") Long id) {
        try {
            userService.switchLockUserById(id);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("OK");
    }
}
