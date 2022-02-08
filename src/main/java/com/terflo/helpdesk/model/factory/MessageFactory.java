package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.MessageService;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Фабрика классов Message to MessageDTO and MessageDTO to Message
 */
@Component
public class MessageFactory {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    UserRequestService userRequestService;

    public Message convertToMessage(MessageDTO messageDTO) throws UserNotFoundException, UserRequestNotFoundException {
        return new Message(
                null,
                userService.findUserById(messageDTO.sender),
                userRequestService.findUserRequestByID(messageDTO.userRequest),
                messageDTO.message,
                messageDTO.date,
                messageDTO.status);
    }

    public MessageDTO convertToMessageDTO(Message message) {
        return new MessageDTO(
                message.getSender().getId(),
                message.getUserRequest().getId(),
                message.getMessage(),
                message.getDate(),
                message.getStatus()
        );
    }

}
