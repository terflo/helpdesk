package com.terflo.helpdesk.model.factories;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.services.UserRequestService;
import com.terflo.helpdesk.model.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
public class MessageDTOFactory {

    private final UserDTOFactory userDTOFactory;

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
}
