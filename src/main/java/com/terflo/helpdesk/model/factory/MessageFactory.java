package com.terflo.helpdesk.model.factory;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.1
 * Фабрика классов Message to MessageDTO and MessageDTO to Message
 */
@Component
public class MessageFactory {

    private final UserService userService;

    private final UserRequestService userRequestService;

    private final UserDTOFactory userDTOFactory;

    public MessageFactory(UserService userService, UserRequestService userRequestService, UserDTOFactory userDTOFactory) {
        this.userService = userService;
        this.userRequestService = userRequestService;
        this.userDTOFactory = userDTOFactory;
    }

    public Message convertToMessage(MessageDTO messageDTO) throws UserNotFoundException, UserRequestNotFoundException {
        return new Message(
                null,
                userService.findUserById(messageDTO.sender.id),
                userRequestService.findUserRequestByID(messageDTO.userRequest),
                messageDTO.message,
                messageDTO.date,
                messageDTO.status);
    }

    public MessageDTO convertToMessageDTO(Message message) {
        return new MessageDTO(
                userDTOFactory.convertToUserDTO(message.getSender()),
                message.getUserRequest().getId(),
                message.getMessage(),
                message.getDate(),
                message.getStatus()
        );
    }

    public List<MessageDTO> convertToMessageDTO(List<Message> messages) {
        return messages
                .stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    public Message getCloseRequestMessage(User operator, Long id) throws UserRequestNotFoundException {
        return new Message(
                null,
                operator,
                userRequestService.findUserRequestByID(id),
                "Запрос закрыт",
                new Date(),
                MessageStatus.RECEIVED
        );
    }

}
