package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.UserRequestAlreadyHaveOperatorException;
import com.terflo.helpdesk.model.exceptions.UserRequestClosedException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import com.terflo.helpdesk.model.repositories.UserRequestRepository;
import com.terflo.helpdesk.model.services.interfaces.UserRequestService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Danil Krivoschiokov
 * @version 1.4
 * Сервис для работы с запросами пользователй
 */
@Service
@AllArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    /**
     * Репозиторий с обращениями пользователей
     */
    private final UserRequestRepository userRequestRepository;

    /**
     * Метод поиска всех запросов созданным пользователем
     * @param user пользователь
     * @return список запросов пользователя
     */
    @Override
    public List<UserRequest> findAllUserRequestsByUser(@NonNull User user)  {
        return userRequestRepository.findAllByUser(user);
    }

    /**
     * Метод поиска всех запросов пользователей, который решает данные оператор
     * @param operator оператор, который прикреплен к запросом
     * @return список всех запросов связанные с указанным оператором
     */
    @Override
    public List<UserRequest> findAllUserRequestsByOperator(User operator) {
        return userRequestRepository.findAllByOperatorOrderByStatus(operator);
    }

    /**
     * Метод поиска всех запросов пользователей
     * @return список всех запросов
     */
    @Override
    public List<UserRequest> findAll() {
        return (List<UserRequest>) userRequestRepository.findAll();
    }

    /**
     * Метод поиска запроса по уникальному индификатору
     * @param id уникальный индификатор
     * @return запрос пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя
     */
    @Override
    public UserRequest findUserRequestByID(@NonNull Long id) throws UserRequestNotFoundException {
        return userRequestRepository.
                findById(id).
                orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не найден"));
    }

    /**
     * Метод добавления запроса пользователя в базу данных
     * @param userRequest сохраняемый запрос пользователя
     */
    @Override
    public UserRequest saveUserRequest(@NonNull UserRequest userRequest) {
        return userRequestRepository.save(userRequest);
    }

    /**
     * Метод приявязки запроса к оператору и изменения его статуса
     * @param id уникальный индификатор запроса
     * @param operator пользователь системы
     * @throws UserRequestNotFoundException возникает при ненахождении оператора
     * @throws UserRequestAlreadyHaveOperatorException возникает при установки оператора к запросу уже имеющий оператора
     * @throws UserRequestClosedException возникает когда запрос уже закрыл
     */
    @Override
    @Transactional
    public UserRequest acceptRequest(@NonNull Long id, @NonNull User operator) throws UserRequestNotFoundException, UserRequestAlreadyHaveOperatorException, UserRequestClosedException {
        UserRequest userRequest = userRequestRepository.findById(id).orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"));

        if(userRequest.getStatus() == RequestStatus.CLOSED)
            throw new UserRequestClosedException("Данный запрос уже закрыт");

        if(userRequest.getOperator() == null) {
            userRequest.setOperator(operator);
            userRequest.setStatus(RequestStatus.IN_PROCESS);
        } else {
            throw new UserRequestAlreadyHaveOperatorException("Данный запрос уже имеет оператора");
        }
        return userRequestRepository.save(userRequest);
    }

    /**
     * Метод изменяет статус запроса пользователя на выбранный
     * @param id уникальный индификатор запроса пользователя
     * @param status статус запроса пользователя
     * @throws UserRequestNotFoundException возникает при ненахождении запроса пользователя в базе данных
     */
    @Override
    @Transactional
    public UserRequest setStatusUserRequestByID(@NonNull Long id, @NonNull RequestStatus status) throws UserRequestNotFoundException {
        UserRequest userRequest = userRequestRepository
                    .findById(id)
                    .orElseThrow(() -> new UserRequestNotFoundException("Запрос пользователя не был найден"));
        userRequest.setStatus(status);
        return userRequestRepository.save(userRequest);
    }

    @Override
    @Transactional
    public void deleteByID(@NonNull Long id) throws UserRequestNotFoundException {
        if(userRequestRepository.findById(id).isPresent())
            userRequestRepository.deleteById(id);
        else
            throw new UserRequestNotFoundException("Обращение пользователя не найдено");
    }

    @Override
    @Transactional
    public void deleteAllByUser(@NonNull User user) throws UserRequestNotFoundException {
        if(userRequestRepository.findAllByUser(user).isEmpty())
            throw new UserRequestNotFoundException("Обращения пользователя не найдено");
        else
            userRequestRepository.deleteAllByUser(user);
    }
}
