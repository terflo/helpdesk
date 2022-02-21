package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.Notification;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.factory.MessageFactory;
import com.terflo.helpdesk.model.services.MessageService;
import com.terflo.helpdesk.model.services.UserRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * Контроллер чата для общения пользователей в запросах
 * в том числе по STOMP поверх SockJS протоколу
 */
@Controller
public class ChatController {

    private final MessageService messageService;

    private final MessageFactory messageFactory;

    private final UserRequestService userRequestService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, MessageFactory messageFactory, UserRequestService userRequestService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messageFactory = messageFactory;
        this.userRequestService = userRequestService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Метод обработки входящего на сервер сообщения от пользователей
     * @param messageDTO сообщение от пользователей
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователля в базе данных
     */
    @MessageMapping("/chat")
    public ResponseEntity<String> processMessage(@Payload MessageDTO messageDTO) {

        //Если запрос закрыт, то игнорируем
        try {
            if(userRequestService.findUserRequestByID(messageDTO.userRequest).getStatus() == RequestStatus.CLOSED)
                return ResponseEntity.badRequest().body("Обращение закрыто");
        } catch (UserRequestNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        //Если сообщение пустое, то игнорируем
        if(messageDTO.message.trim().isEmpty())
            return ResponseEntity.ok().body("\"\"");

        messageDTO.date = new Date();
        messageDTO.status = MessageStatus.RECEIVED;

        //Конвертируем сообщение в нормальный вид из DTO вида,
        //сохраняем в базе данных и отправляем подписчикам уведомление о новом сообщении
        try {
            Message message = messageFactory.convertToMessage(messageDTO);
            messageService.saveMessage(message);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(messageDTO.userRequest),"/queue/messages",
                    new Notification(
                            message.getId(),
                            message.getUserRequest().getId(),
                            message.getSender().getId()));
        } catch (UserNotFoundException | UserRequestNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("\"\"");
    }

    /**
     * Метод для поучения кол-ва новых сообщений
     * @param userRequest уникальный индификатор запроса пользователя
     * @return кол-во новых сообщений
     */
    @GetMapping("/messages/{userRequest}/count")
    public ResponseEntity<?> countNewMessages(@PathVariable Long userRequest) {

        try {
            return ResponseEntity.ok(messageService.countNewMessages(userRequest));
        } catch (UserRequestNotFoundException e) {
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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
