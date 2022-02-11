package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
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

    @Autowired
    private UserRequestService userRequestService;

    /**
     * Метод для подсчёта кол-ва новых сообщний
     * @param userRequestID уникальный индификатор запроса пользователя
     * @return кол-во новых сообщений
     */
    public Long countNewMessages(Long userRequestID) throws UserRequestNotFoundException {
        return messageRepository
                .findAllByUserRequest(userRequestService.findUserRequestByID(userRequestID))
                .stream()
                .filter((message -> message.getStatus() == MessageStatus.RECEIVED))
                .count();
    }

    /**
     * Метод поиска сообщения в базе данных по ID
     * @param id уникальный индификатор сообщения
     * @return сообщение
     * @throws MessageNotFoundException возникает при ненахождении сообщения
     */
    public Message findMessageByID(Long id) throws MessageNotFoundException {
        return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException("Сообщение не найдено"));
    }

    /**
     * Метод поиска сообщений, находящиеся в запросе пользователя
     * @param userRequest запрос пользователя
     * @return список сообщений
     * @throws MessageNotFoundException возникает при ненахождении сообщений
     */
    public List<Message> findMessagesByUserRequest (UserRequest userRequest) throws MessageNotFoundException {
        List<Message> messages = messageRepository.findAllByUserRequest(userRequest);
        if(messages.isEmpty())
            throw new MessageNotFoundException("Сообщения не найдены");
        else
            return messages;
    }

    /**
     * Метод сохранения сообщения в базу данных
     * @param message сохраняемое сообщение
     */
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

}
