package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.repositories.MessageRepository;
import com.terflo.helpdesk.model.repositories.UserRepository;
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
     * Репозиторий пользователей
     */
    @Autowired
    private UserRepository userRepository;

    public List<Message> findMessagesBySenderID (Long id) {
        return null;
    }

}
