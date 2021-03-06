package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.repositories.MessageRepository;
import com.terflo.helpdesk.model.services.interfaces.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.3
 * Сервис для работы с сообщениями
 */
@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    /**
     * Репозиторий сообщений
     */
    private final MessageRepository messageRepository;

    /**
     * Метод для подсчёта кол-ва новых сообщний
     * @param userRequest запрос пользователя
     * @return кол-во новых сообщений
     */
    @Override
    public Long countNewMessagesByUserRequest(UserRequest userRequest) {
        return messageRepository
                .findAllByUserRequestOrderByDate(userRequest)
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
    @Override
    public Message findMessageByID(Long id) throws MessageNotFoundException {
        return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException("Сообщение не найдено"));
    }

    /**
     * Метод поиска сообщений, находящиеся в запросе пользователя
     * @param userRequest запрос пользователя
     * @return список сообщений
     */
    @Override
    public List<Message> findMessagesByUserRequest (UserRequest userRequest) {
        return messageRepository.findAllByUserRequestOrderByDate(userRequest);
    }

    /**
     * Метод сохранения сообщения в базу данных
     * @param message сохраняемое сообщение
     */
    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
