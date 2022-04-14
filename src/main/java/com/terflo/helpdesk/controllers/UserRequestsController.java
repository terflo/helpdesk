package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.*;
import com.terflo.helpdesk.model.entity.dto.UserRequestDTO;
import com.terflo.helpdesk.model.entity.enums.FreeRequestStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.factories.MessageFactory;
import com.terflo.helpdesk.model.factories.UserFactory;
import com.terflo.helpdesk.model.factories.UserRequestFactory;
import com.terflo.helpdesk.model.services.interfaces.MessageService;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
 * Контроллер запросов пользователей на решение какой-либо проблемы
 */
@Log4j2
@Controller
@AllArgsConstructor
public class UserRequestsController {

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Сервис для работы с обращениями пользователей
     */
    private final UserRequestService userRequestService;

    /**
     * Сервис для работы с сообщениями пользователей
     */
    private final MessageService messageService;

    /**
     * Фабрика обращений пользователей
     */
    private final UserRequestFactory userRequestFactory;

    /**
     * Шаблон сообщения
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Фабрика сообщений пользователей
     */
    private final MessageFactory messageFactory;

    /**
     * Фабрика пользователей
     */
    private final UserFactory userFactory;

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
        model.addAttribute("user", userFactory.convertToUserDTO(user));
        model.addAttribute("requests", userRequestFactory.convertToUserRequestDTO(userRequests));
        return "request/requests";
    }

    /**
     * Контроллер GET-запроса на вывод страницы с добавление нового обращения
     * @return название страницы
     */
    @GetMapping("/requests/add")
    public String requests_add() {
        return "request/add";
    }

    /**
     * Контроллер GET-запроса на принятия курирования запроса пользователя оператором
     * @param id индификатор запроса пользователя
     * @param authentication данные аунтефикации клиента
     * @return результат запроса
     */
    @ResponseBody
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable(value = "id") Long id, Authentication authentication) {

        try {

            User operator = userService.findUserByUsername(authentication.getName());
            UserRequest userRequest = userRequestService.acceptRequest(id, operator);
            Message message = messageFactory.generateAcceptRequestMessage(operator, userRequest);

            message = messageService.saveMessage(message);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(id), "/queue/messages",
                    new Notification(
                            message.getId(),
                            message.getUserRequest().getId(),
                            message.getSender().getId()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/requests/free",
                    new FreeRequestNotification(FreeRequestStatus.ACCEPTED, id, null)
            );

        } catch (UserRequestNotFoundException | UserRequestAlreadyHaveOperatorException | UserNotFoundException | UserRequestClosedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        log.info("Обращение #" + id + " принято пользователем " + authentication.getName());
        return ResponseEntity.ok("\"\"");
    }

    /**
     * Контроллер GET-запроса на вывод страницы свободных запросов (без операторов)
     * @param model еременные для отрисовки страницы
     * @return название страницы
     */
    @GetMapping("/requests/free")
    public String freeRequests(Model model) {
        List<UserRequest> requests = userRequestService.findAllUserRequestsByStatus(RequestStatus.BEGINNING);
        model.addAttribute("requests", userRequestFactory.convertToUserRequestDTO(requests));
        return "request/free";
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
        model.addAttribute("requests", userRequestService.findAllUserRequestsByOperator(operator));
        model.addAttribute("name", operator.getUsername());
        return "request/supervised";
    }

    /**
     * Контроллер POST-запроса для создания запроса пользователя
     * @param authentication информация о пользователе
     * @param userRequestDTO тело запроса пользователя (название, описание, приоритет и т.п.)
     * @return страница с запросами
     */
    @ResponseBody
    @PostMapping("/requests")
    public ResponseEntity<String> addRequest(Authentication authentication,
                                             @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {

            UserRequest userRequest = userRequestFactory.convertToUserRequest(userRequestDTO);
            userRequest.setStatus(RequestStatus.BEGINNING); //устанавливаем статус что обращение принято в систему
            userRequest.setUser(userService.findUserByUsername(authentication.getName()));  //устанавливаем отправителя обращения
            userRequest.setDate(new Date());    //устанавливаем дату обращения

            userRequest = userRequestService.saveUserRequest(userRequest);

            messagingTemplate.convertAndSend("/requests/free", new FreeRequestNotification(
                    FreeRequestStatus.NEW,
                    userRequest.getId(),
                    userRequestFactory.convertToUserRequestDTO(userRequest)
            ));

            return ResponseEntity.ok().body("\"\"");

        } catch (UserNotFoundException e) { //в случае если пользователь не был найден
            return ResponseEntity.badRequest().body("\"" + e.getMessage() + "\"");
        }
    }

    /**
     * Метод GET-запроса завершения запросов пользователя
     * @param id уникальный индификатор запроса
     * @return страница с запросами
     */
    @ResponseBody
    @PostMapping("/requests/{id}/close")
    public ResponseEntity<String> closeRequest(@PathVariable(value = "id") Long id, Authentication authentication) {

        try {

            UserRequest userRequest = userRequestService.setStatusUserRequestByID(id, RequestStatus.CLOSED);
            User operator = userService.findUserByUsername(authentication.getName());
            Message message = messageFactory.generateCloseRequestMessage(operator, userRequest);

            message = messageService.saveMessage(message);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(id),"/queue/messages",
                    new Notification(
                            message.getId(),
                            message.getUserRequest().getId(),
                            message.getSender().getId()));
        } catch (UserNotFoundException | UserRequestNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        log.info(String.format("Пользователь %s закрыл обращение #%s", authentication.getName(), id));

        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * POST запрос на удаление обращения пользователя
     * @param id уникальный индификатор обращения
     * @return страница с обращениями
     */
    @ResponseBody
    @DeleteMapping("/requests/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable(value = "id") Long id, Authentication authentication) {

        try {
            userRequestService.deleteByID(id);
        } catch (UserRequestNotFoundException e) {
            log.error(e.getMessage());
            ResponseEntity.badRequest().body(e.getMessage());
        }

        log.info(String.format("Обращение #%s удалено пользователем %s", id, authentication.getName()));
        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * Метод GET-запроса просмотра запроса пользователя
     * @param id уникальный индификатор запроса
     * @param model переменные для отрисовки страницы
     * @return страница с запросом пользователя
     */
    @GetMapping("/requests/{id}")
    public String showRequest(@PathVariable(value = "id") Long id, Model model, Authentication authentication) {

        try {

            UserRequest userRequest = userRequestService.findUserRequestByID(id);
            User user = userService.findUserByUsername(authentication.getName());

            List<Message> messages = messageService.findMessagesByUserRequest(userRequest);
            HashMap<Long, String> avatarsBase64 = new HashMap<>();

            for (Message message : messages) {
                Long senderID = message.getSender().getId();
                Image image = message.getSender().getAvatar();
                avatarsBase64.put(senderID, image.getBase64ImageWithType());
            }

            model.addAttribute("userRequest", userRequestFactory.convertToUserRequestDTO(userRequest));
            model.addAttribute("user", userFactory.convertToUserDTO(user));
            model.addAttribute("messages", messageFactory.convertToMessageDTO(messages));
            model.addAttribute("avatars", avatarsBase64);

            return "request/request";

        } catch (UserRequestNotFoundException | UserNotFoundException e) {
            log.error(e.getMessage());
            model.addAttribute("status", 404);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("trace", Arrays.toString(e.getStackTrace()));
            return "error";
        }
    }

    @ResponseBody
    @MessageMapping("/free")
    public ResponseEntity<String> freeRequests(@Payload FreeRequestNotification freeRequestNotification) {

        log.warn("В сокет свободных обращений пришло сообщение: " + freeRequestNotification.toString());
        messagingTemplate.convertAndSend("/free", freeRequestNotification);
        return ResponseEntity.ok().body("\"\"");
    }
}
