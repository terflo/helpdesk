package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * Фабрика классов Message в MessageDTO
 */
@Component
@AllArgsConstructor
public class MessageFactory {

    private final UserFactory userFactory;

    private final ImageFactory imageFactory;

    private final UserService userService;

    private final UserRequestService userRequestService;

    public Message convertToNewMessage(MessageDTO messageDTO) throws UserNotFoundException, UserRequestNotFoundException, IOException {
        return new Message(
                null,
                messageDTO.sender == null ? null : userService.findUserById(messageDTO.sender.id),
                messageDTO.userRequest == null ? null : userRequestService.findUserRequestByID(messageDTO.userRequest),
                messageDTO.message,
                messageDTO.date,
                messageDTO.status,
                messageDTO.imageBase64 == null ? null : imageFactory.getImage(messageDTO.imageBase64));
    }

    public MessageDTO convertToMessageDTO(Message message) {
        return new MessageDTO(
                userFactory.convertToUserDTO(message.getSender()),
                message.getUserRequest().getId(),
                message.getMessage(),
                message.getDate(),
                message.getStatus(),
                message.getImage() != null ? message.getImage().getBase64ImageWithType() : null
        );
    }

    public List<MessageDTO> convertToMessageDTO(List<Message> messages) {
        return messages
                .stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    public Message generateCloseRequestMessage(User operator, UserRequest userRequest) {
        return new Message(
                null,
                operator,
                userRequest,
                String.format("Обращение закрыто оператором %s", operator.getUsername()),
                new Date(),
                MessageStatus.RECEIVED,
                null
        );
    }

    public Message generateAcceptRequestMessage(User operator, UserRequest userRequest) {
        return new Message(
                null,
                operator,
                userRequest,
                String.format("Оператор %s принял обращение в обработку", operator.getUsername()),
                new Date(),
                MessageStatus.RECEIVED,
                null
        );
    }
}
