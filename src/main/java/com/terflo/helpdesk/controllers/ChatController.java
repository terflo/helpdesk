package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.Notification;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.factories.MessageFactory;
import com.terflo.helpdesk.model.services.interfaces.MessageService;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * Контроллер чата для общения пользователей в запросах
 * в том числе по STOMP поверх SockJS протоколу
 */
@Log4j2
@Controller
@AllArgsConstructor
public class ChatController {

    /**
     * Сервис для работы с сообщениями
     */
    private final MessageService messageService;

    /**
     * Фабрика сообщений
     */
    private final MessageFactory messageFactory;

    /**
     * Сервис обращений пользователей
     */
    private final UserRequestService userRequestService;

    /**
     * Шаблон сообщений
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Метод обработки входящего на сервер сообщения от пользователей
     * @param messageDTO сообщение от пользователей
     */
    @ResponseBody
    @MessageMapping("/chat")
    public ResponseEntity<String> processMessage(@Valid @RequestBody MessageDTO messageDTO) {

        //Если запрос закрыт, то игнорируем
        try {

            UserRequest userRequest = userRequestService.findUserRequestByID(messageDTO.userRequest);

            if(userRequest.getStatus().equals(RequestStatus.CLOSED)) {
                log.error("Попытка отправить сообщение в закрытое обращение пользователем " + Objects.requireNonNull(messageDTO.sender).username);
                return ResponseEntity.badRequest().body("Обращение закрыто");
            }
        } catch (UserRequestNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        messageDTO.date = new Date();
        messageDTO.status = MessageStatus.RECEIVED;

        //Конвертируем сообщение в нормальный вид из DTO вида,
        //сохраняем в базе данных и отправляем подписчикам уведомление о новом сообщении
        try {
            Message message = messageService.saveMessage(messageFactory.convertToNewMessage(messageDTO));
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(messageDTO.userRequest),"/queue/messages",
                    new Notification(
                            message.getId(),
                            message.getUserRequest().getId(),
                            message.getSender().getId()));
        } catch (UserNotFoundException | UserRequestNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        log.info("Сообщение от пользователя " + messageDTO.sender.username + " успешно обработано");
        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * Метод для поучения кол-ва новых сообщений
     * @param userRequestID уникальный индификатор запроса пользователя
     * @return кол-во новых сообщений
     */
    @GetMapping("/messages/{userRequestID}/count")
    public ResponseEntity<?> countNewMessages(@PathVariable(name = "userRequestID") Long userRequestID) {

        try {
            UserRequest userRequest = userRequestService.findUserRequestByID(userRequestID);
            return ResponseEntity.ok(messageService.countNewMessagesByUserRequest(userRequest));
        } catch (UserRequestNotFoundException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Метод получения сообщения пользователем из клиента
     * @param id уникальный индификатор сообщения
     * @return Сообщение для пользователя
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage (@PathVariable Long id) {

        try {
            Message message = messageService.findMessageByID(id);
            message.setStatus(MessageStatus.DELIVERED);
            return ResponseEntity.ok(messageFactory.convertToMessageDTO(message));
        } catch (MessageNotFoundException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
