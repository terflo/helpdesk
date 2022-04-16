package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.repositories.UserRepository;
import com.terflo.helpdesk.model.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Danil Krivoschiokov
 * @version 1.4
 * Сервис для работы с пользователями
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;

    /**
     * Класс управляющий пользователями и их сеансами
     */
    private final SessionRegistry sessionRegistry;


    /**
     * Функция поиска пользователя по индификатору
     *
     * @param id индификатор пользователя
     * @return найденный пользователь из базы данных
     */
    public User findUserById(Long id) throws UserNotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Пользователь #%s не найден", id))
                );
    }

    /**
     * Функция поиска пользователя по имени
     *
     * @param username имя пользователя
     * @return пользователь
     * @throws UserNotFoundException возникает при ненахождении пользователя
     */
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Пользователь %s не найден", username))
                );
    }

    /**
     * Метод поиска пользователя в базе данных по email
     * @param email email пользователя
     * @return пользователь
     * @throws UserNotFoundException возникает в случае не нахождения пользователя
     */
    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("Пользователь с email %s не найден", email))
                );
    }

    /**
     * Функция ищет пользователя в базе данных
     *
     * @param username имя пользователя
     * @return результат поиска пользователя
     */
    public boolean userIsExistByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .isPresent();
    }

    /**
     * Функция ищет пользователя в базе данных
     *
     * @param email email пользователя
     * @return результат поиска пользователя
     */
    public boolean userIsExistByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .isPresent();
    }

    /**
     * Функция поиска всех пользователей из базы данных
     *
     * @return все пользователи из базы данных
     */
    public List<User> getAllUsers() {
        return StreamSupport
                .stream(
                        userRepository.findAll().spliterator(),
                        false)
                .collect(Collectors.toList());
    }

    /**
     * Метод поиска не истёкших сессий пользователей
     * @return список имен активных пользователей
     */
    public List<String> getActiveUsernamesFromSessionRegistry() {
        return sessionRegistry
                .getAllPrincipals()
                .stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(o -> ((User) o).getUsername())
                .collect(Collectors.toList());
    }

    /**
     * Метод добавляет пользователю новую роль
     * @param id уникальный индификатор пользователя
     * @param role добавляемая роль
     * @throws UserNotFoundException возникает в случае не нахождении пользователя
     */
    public void addRoleToUser(Long id, Role role) throws UserNotFoundException {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                    () -> new UserNotFoundException(String.format("Пользователь #%s не найден", id))
                );
        user.getRoles().add(role);
        userRepository.save(user);
    }

    /**
     * Метод забирает у пользователя роль
     * @param id уникальный индификатор пользователя
     * @param role удаляемая роль
     * @throws UserNotFoundException возникает в случае не нахождении пользоввателя
     */
    public void deleteRoleToUser(Long id, Role role) throws UserNotFoundException {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Пользователь #%s не найден", id))
                );
        user.getRoles().remove(role);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void deleteUser(User user) throws UserNotFoundException {
        if(!userRepository.existsById(user.getId()))
            throw new UserNotFoundException(String.format("Пользователь #%s не найден", user.getId()));
        else userRepository.deleteById(user.getId());
    }


    @Override
    @Transactional
    public void deleteUser(List<User> users) throws UserNotFoundException {
        for(User user : users) {
            this.deleteUser(user);
        }
    }


    public User saveUser(User user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UserAlreadyExistException(
                    String.format("Пользователь %s уже существует", user.getUsername())
            );
        return userRepository.save(user);
    }

    /**
     * Метод обновления данных пользователя в базе
     * @param user обновляемый пользователь
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     */
    @Transactional
    public void updateUser(User user) throws UserNotFoundException {
        if(userRepository.findById(user.getId()).isPresent())
            userRepository.save(user);
        else
            throw new UserNotFoundException(
                    String.format("Пользователь %s не найден", user.getUsername())
            );
    }

    /**
     * Метод переключает блокировку аккаунта пользователя (блокирован/разблокирован)
     * @param id уникальный индификатор пользователя
     * @throws UserNotFoundException возникает при ненахождении пользователя
     */
    @Transactional
    public void switchLockUserById(Long id) throws UserNotFoundException {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь #%s не найден", id)
                        ));
        user.switchLock();
        userRepository.save(user);
    }

    @Transactional
    public void activateUserByUsername(String username) throws UserNotFoundException, UserAlreadyActivatedException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь %s не найден", username)
                ));

        if(user.isEnabled())
            throw new UserAlreadyActivatedException(
                    String.format("Пользователь %s уже активирован", username)
            );

        user.setEnabled(true);
        userRepository.save(user);
    }


    /**
     * Метод удаляет пользователя из базы данных
     * @param id уникальный индификатор пользователя
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     */
    @Override
    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new UserNotFoundException(String.format("Пользователь #%s не найден", id));
        userRepository.deleteById(id);
    }


    /**
     * Метод удаляет пользователя из базы данных
     * @param username имя пользователя
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     */
    @Override
    @Transactional
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent())
            throw new UserNotFoundException(String.format("Пользователь %s не найден", username));
        userRepository.deleteUserByUsername(username);
    }


    /**
     * Функция поиска пользователя по имени
     * @param s имя пользователя
     * @return найденный пользователь
     * @throws UsernameNotFoundException возникает при ненахождения пользователя в базе данных
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь %s не найден", s)));
    }
}
