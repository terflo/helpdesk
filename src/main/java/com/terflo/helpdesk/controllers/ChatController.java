package com.terflo.helpdesk.controllers;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.Notification;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.factory.MessageFactory;
import com.terflo.helpdesk.model.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Метод обработки входящего на сервер сообщения от пользователей
     * @param messageDTO сообщение от пользователей
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователля в базе данных
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO messageDTO) throws UserNotFoundException, UserRequestNotFoundException {

        messageDTO.date = new Date();
        messageDTO.status = MessageStatus.RECEIVED;

        Message message = messageFactory.convertToMessage(messageDTO);
        messageService.saveMessage(message);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDTO.userRequest),"/queue/messages",
                new Notification(
                        message.getId(),
                        message.getUserRequest().getId(),
                        message.getSender().getId()));

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
