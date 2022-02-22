package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.UserRequestAlreadyClosed;
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

    private final UserRequestRepository userRequestRepository;

    public UserRequestService(UserRequestRepository userRequestRepository) {
        this.userRequestRepository = userRequestRepository;
    }

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

    /**
     * Метод приявязки запроса к оператору и изменения его статуса
     * @param id уникальный индификатор запроса
     * @param operator пользователь системы
     * @throws UserRequestNotFoundException возникает при ненахождении оператора
     * @throws UserRequestAlreadyHaveOperatorException возникает при установки оператора к запросу уже имеющий оператора
     * @throws UserRequestClosedException возникает когда запрос уже закрыл
     */
    public void acceptRequest(Long id, User operator) throws UserRequestNotFoundException, UserRequestAlreadyHaveOperatorException, UserRequestClosedException {
        UserRequest userRequest = userRequestRepository.findById(id).orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"));

        if(userRequest.getStatus() == RequestStatus.CLOSED)
            throw new UserRequestClosedException("Данный запрос уже закрыт");

        if(userRequest.getOperator() == null) {
            userRequest.setOperator(operator);
            userRequest.setStatus(RequestStatus.IN_PROCESS);
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
        return userRequestRepository.findAllByOperatorOrderByStatus(operator);
    }

    /**
     * Метод поиска всех запросов пользователей
     * @return список всех запросов
     */
    public List<UserRequest> findAll() {
        return (List<UserRequest>) userRequestRepository.findAll();
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
     * Метод изменяет статус запроса пользователя на выбранный
     * @param id уникальный индификатор запроса пользователя
     * @param status статус запроса пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя в базе данных
     */
    @Transactional
    public void setStatusUserRequestByID(Long id, RequestStatus status) throws UserRequestNotFoundException, UserRequestAlreadyClosed {

        UserRequest userRequest = userRequestRepository.
                findById(id).
                orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"));

        if(userRequest.getStatus() == RequestStatus.CLOSED) throw new UserRequestAlreadyClosed("Обращение пользователя уже закрыто");

            userRequestRepository
                    .findById(id)
                    .orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"))
                    .setStatus(status);
    }

    @Transactional
    public void deleteByID(Long id) throws UserRequestNotFoundException {
        if(userRequestRepository.findById(id).isPresent())
            userRequestRepository.deleteById(id);
        else
            throw new UserRequestNotFoundException("Обращение пользователя не найдено");
    }

    @Transactional
    public void deleteAllByUser(User user) throws UserRequestNotFoundException {
        if(userRequestRepository.findAllByUser(user).isEmpty())
            throw new UserRequestNotFoundException("Обращения пользователя не найдено");
        else
            userRequestRepository.deleteAllByUser(user);
    }
}
