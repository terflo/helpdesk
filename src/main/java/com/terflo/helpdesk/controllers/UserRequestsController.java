package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.*;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.factory.MessageFactory;
import com.terflo.helpdesk.model.factory.UserDTOFactory;
import com.terflo.helpdesk.model.factory.UserRequestDTOFactory;
import com.terflo.helpdesk.model.services.MessageService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Контроллер запросов пользователей на решение какой-либо проблемы
 */
@Log4j2
@Controller
public class UserRequestsController {

    private final UserService userService;

    private final UserRequestService userRequestService;

    private final MessageService messageService;

    private final UserRequestDTOFactory userRequestDTOFactory;

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageFactory messageFactory;

    private final UserDTOFactory userDTOFactory;

    public UserRequestsController(UserService userService, UserRequestService userRequestService, MessageService messageService, UserRequestDTOFactory userRequestDTOFactory, SimpMessagingTemplate messagingTemplate, MessageFactory messageFactory, UserDTOFactory userDTOFactory) {
        this.userService = userService;
        this.userRequestService = userRequestService;
        this.messageService = messageService;
        this.userRequestDTOFactory = userRequestDTOFactory;
        this.messagingTemplate = messagingTemplate;
        this.messageFactory = messageFactory;
        this.userDTOFactory = userDTOFactory;
    }

    /**
     * Контроллер GET-запроса на вывод страницы
     * @param authentication информация о пользователе
     * @param model переменные для отрисовки страницы
     * @return название страницы
     */
    @GetMapping("/requests")
    public String requests(Authentication authentication, Model model) {
        User user;
        try {
            user = userService.findUserByUsername(authentication.getName());
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            model.addAttribute("status", 404);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
            return "error";
        }
        List<UserRequest> userRequests = userRequestService.findAllUserRequestsByUser(user);
        model.addAttribute("user", userDTOFactory.convertToUserDTO(user));
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(userRequests));
        return "requests";
    }

    /**
     * Контроллер GET-запроса на вывод страницы
     * @return название страницы
     */
    @GetMapping("/requests/add")
    public String requests_add() {
        return "request-add";
    }

    /**
     * Контроллер GET-запроса на принятия курирования запроса пользователя оператором
     * @param id индификатор запроса пользователя
     * @param authentication данные аунтефикации клиента
     * @return результат запроса
     */
    @PostMapping("/requests/accept/{id}")
    public ResponseEntity<String> acceptRequest(@PathVariable(value = "id") Long id, Authentication authentication) {

        try {
            User operator = userService.findUserByUsername(authentication.getName());
            userRequestService.acceptRequest(id, operator);
        } catch (UserRequestNotFoundException | UserRequestClosedException | UserRequestAlreadyHaveOperatorException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    /**
     * Контроллер GET-запроса на вывод страницы свободных запросов (без операторов)
     * @param model еременные для отрисовки страницы
     * @return название страницы
     */
    @GetMapping("/requests/free")
    public String freeRequests(Model model) {
        List<UserRequest> requests = userRequestService.findAllNonOperatorRequests();
        model.addAttribute("requests", userRequestDTOFactory.convertToUserRequestDTO(requests));
        return "requests-free";
    }

    /**
     * Контроллер GET-запроса для отображения курируемых запросов пользователей
     * @param model переменные для отображения страницы
     * @param authentication данные аунтификации пользователя
     * @return страница с списком курируемых запросов
     */
    @GetMapping("/requests/supervised")
    public String supervisedRequests(Model model, Authentication authentication) {
        User operator;
        try {
            operator = userService.findUserByUsername(authentication.getName());
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            model.addAttribute("status", 404);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
            return "error";
        }
        model.addAttribute("requests", userRequestService.findUserRequestsByOperator(operator));
        model.addAttribute("name", operator.getUsername());
        return "requests-supervised";
    }

    /**
     * Контроллер POST-запроса для создания запроса пользователя
     * @param authentication информация о пользователе
     * @param userRequest тело запроса пользователя (название, описание, приоритет и т.п.)
     * @return страница с запросами
     */
    @PostMapping("/requests/add")
    public ResponseEntity<String> addRequest(Authentication authentication, @RequestBody UserRequest userRequest) {

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
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        if(errors.isEmpty()) {
            userRequest.setDate(new Date());
            userRequestService.saveUserRequest(userRequest);
            return ResponseEntity.ok().body("\"\"");
        } else {
            return ResponseEntity.badRequest().body(errors.toString());
        }
    }

    /**
     * Метод GET-запроса завершения запросов пользователя
     * @param id уникальный индификатор запроса
     * @return страница с запросами
     */
    @PostMapping("/requests/close/{id}")
    public ResponseEntity<String> closeRequest(@PathVariable(value = "id") Long id, Authentication authentication) {

        try {
            userRequestService.setStatusUserRequestByID(id, RequestStatus.CLOSED);
            User operator = userService.findUserByUsername(authentication.getName());
            Message message = messageFactory.getCloseRequestMessage(operator, id);
            messageService.saveMessage(message);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(id),"/queue/messages",
                    new Notification(
                            message.getId(),
                            message.getUserRequest().getId(),
                            message.getSender().getId()));
        } catch (UserNotFoundException | UserRequestNotFoundException | UserRequestAlreadyClosed e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("\"\"");
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
            ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Метод GET-запроса просмотра запроса пользователя
     * @param id уникальный индификатор запроса
     * @param model переменные для отрисовки страницы
     * @return страница с запросом пользователя
     */
    @GetMapping("/requests/{id}")
    public String showRequest(@PathVariable(value = "id") Long id, Model model, Authentication authentication) {

        UserRequest userRequest;
        User user;
        try {
            userRequest = userRequestService.findUserRequestByID(id);
            user = userService.findUserByUsername(authentication.getName());
        } catch (UserRequestNotFoundException | UserNotFoundException e) {
            log.error(e.getMessage());
            model.addAttribute("status", 404);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
            return "error";
        }

        List<Message> messages = messageService.findMessagesByUserRequest(userRequest);
        HashMap<Long, String> avatarsBase64 = new HashMap<>();

        for (Message message : messages) {
            Long senderID = message.getSender().getId();
            Image image = message.getSender().getAvatar();
            avatarsBase64.put(senderID, "data:" + image.getType() + ";base64," + image.getBase64Image());
        }

        model.addAttribute("userRequest", userRequestDTOFactory.convertToUserRequestDTO(userRequest));
        model.addAttribute("user", userDTOFactory.convertToUserDTO(user));
        model.addAttribute("messages", messageFactory.convertToMessageDTO(messages));
        model.addAttribute("avatars", avatarsBase64);

        return "request";
    }
}
