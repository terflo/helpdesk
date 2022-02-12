package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.UserRequestAlreadyHaveOperatorException;
import com.terflo.helpdesk.model.exceptions.UserRequestClosedException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.repositories.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сервис для работы с запросами пользователй
 */
@Service
public class UserRequestService {

    @Autowired
    UserRequestRepository userRequestRepository;

    /**
     * Метод поиска всех запросов созданным пользователем
     * @param user пользователь
     * @return список запросов пользователя
     */
    public List<UserRequest> findAllUserRequestsByUser(User user)  {
        return userRequestRepository.findAllByUser(user);
    }

    /**
     * Метод поиска всех запросов, к которым не привязан оператор
     * @return список свободных запросов
     */
    public List<UserRequest> findAllNonOperatorRequests() {
        return userRequestRepository.findAllByOperator(null);
    }

    public void acceptRequest(Long id, User operator) throws UserRequestNotFoundException, UserRequestAlreadyHaveOperatorException, UserRequestClosedException {
        UserRequest userRequest = userRequestRepository.findById(id).orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"));

        if(userRequest.getStatus() == RequestStatus.CLOSED)
            throw new UserRequestClosedException("Данный запрос уже закрыт");

        if(userRequest.getOperator() == null) {
            userRequest.setOperator(operator);
        } else {
            throw new UserRequestAlreadyHaveOperatorException("Данный запрос уже имеет оператора");
        }
        userRequestRepository.save(userRequest);
    }

    /**
     * Метод поиска всех запросов пользователей, который решает данные оператор
     * @param operator оператор, который прикреплен к запросом
     * @return список всех запросов связанные с указанным оператором
     */
    public List<UserRequest> findUserRequestsByOperator(User operator) {
        return userRequestRepository.findAllByOperator(operator);
    }

    /**
     * Метод поиска всех запросов пользователей
     * @return список всех запросов
     * @throws UserRequestNotFoundException возникает при ненахождении запросов
     */
    public List<UserRequest> findAll() throws UserRequestNotFoundException {
        List<UserRequest> userRequests = (List<UserRequest>) userRequestRepository.findAll();
        if(userRequests.isEmpty())
            throw new UserRequestNotFoundException("Список запросов пуст");
        else
            return userRequests;
    }

    /**
     * Метод поиска запроса по уникальному индификатору
     * @param id уникальный индификатор
     * @return запрос пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя
     */
    public UserRequest findUserRequestByID(Long id) throws UserRequestNotFoundException {
        return userRequestRepository.
                findById(id).
                orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не найден"));
    }

    /**
     * Метод добавления запроса пользователя в базу данных
     * @param userRequest сохраняемый запрос пользователя
     */
    public void saveUserRequest(UserRequest userRequest) {
        userRequestRepository.save(userRequest);
    }

    /**
     * Метод изменяет статус запроса пользователя на "закрыто" из базы данных
     * @param id уникальный индификатор запроса пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя в базе данных
     */
    @Transactional
    public void closeUserRequestByID(Long id) throws UserRequestNotFoundException {
        userRequestRepository
                .findById(id)
                .orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"))
                .setStatus(RequestStatus.CLOSED);
    }
}
