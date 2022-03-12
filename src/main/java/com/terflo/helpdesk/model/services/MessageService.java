package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Message;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.dto.MessageDTO;
import com.terflo.helpdesk.model.entity.enums.MessageStatus;
import com.terflo.helpdesk.model.exceptions.MessageNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.repositories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.2
 * Сервис для работы с сообщениями
 */
@Service
@AllArgsConstructor
public class MessageService {

    /**
     * Репозиторий сообщений
     */
    private final MessageRepository messageRepository;

    /**
     * Сервис для работы с обращениями пользователей
     */
    private final UserRequestService userRequestService;

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Метод для подсчёта кол-ва новых сообщний
     * @param userRequestID уникальный индификатор запроса пользователя
     * @return кол-во новых сообщений
     */
    public Long countNewMessages(Long userRequestID) throws UserRequestNotFoundException {
        return messageRepository
                .findAllByUserRequestOrderByDate(userRequestService.findUserRequestByID(userRequestID))
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
     */
    public List<Message> findMessagesByUserRequest (UserRequest userRequest) {
        return messageRepository.findAllByUserRequestOrderByDate(userRequest);
    }

    /**
     * Метод сохранения сообщения в базу данных
     * @param message сохраняемое сообщение
     */
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    /**
     * Метод сохранения сообщения в базу данных
     * @param messageDTO сохраняемое сообщение
     * @return сообщение сохраненное в базе данных
     * @throws UserRequestNotFoundException возникает в случае не нахождения обращения пользователя в базе данных
     * @throws UserNotFoundException возникает в случае не нахождения пользователя в базе данных
     */
    public Message saveMessage(MessageDTO messageDTO) throws UserRequestNotFoundException, UserNotFoundException {
        return messageRepository.save(new Message(
                null,
                userService.findUserById(messageDTO.sender.id),
                userRequestService.findUserRequestByID(messageDTO.userRequest),
                messageDTO.message,
                messageDTO.date,
                messageDTO.status));
    }

    public Message generateCloseRequestMessage(User operator, Long id) throws UserRequestNotFoundException {
        return messageRepository.save(new Message(
                null,
                operator,
                userRequestService.findUserRequestByID(id),
                String.format("Обращение закрыто оператором %s", operator.getUsername()),
                new Date(),
                MessageStatus.RECEIVED
        ));
    }

    public Message generateAcceptRequestMessage(User operator, UserRequest userRequest) throws UserRequestNotFoundException {
        return messageRepository.save(new Message(
                null,
                operator,
                userRequest,
                String.format("Оператор %s принял обращение в обработку", operator.getUsername()),
                new Date(),
                MessageStatus.RECEIVED
        ));
    }
}
