package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сервис для работы с сообщениями
 */
@Service
public class MessageService {

    /**
     * Репозиторий сообщений
     */
    @Autowired
    private MessageRepository messageRepository;

    /**
     * Метод поиска сообщений, находящиеся в запросе пользователя
     * @param userRequest запрос пользователя
     * @return список сообщений
     * @throws MessageNotFoundException возникает при ненахождении сообщений
     */
    public List<Message> findMessagesByUserRequest (UserRequest userRequest) throws MessageNotFoundException {
        List<Message> messages = messageRepository.findMessageByUserRequest(userRequest);
        if(messages.isEmpty())
            throw new MessageNotFoundException("Сообщения не найдены");
        else
            return messages;
    }

}
