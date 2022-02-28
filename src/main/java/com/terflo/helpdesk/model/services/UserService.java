package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.factory.ImageFactory;
import com.terflo.helpdesk.model.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сервис для работы с пользователями
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final DecisionService decisionService;

    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий ролей
     */
    private final RoleService roleService;

    /**
     * Кодировщик паролей
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Класс управляющий пользователями и их сеансами
     */
    private final SessionRegistry sessionRegistry;

    /**
     * Сервис аватаров пользователей
     */
    private final UserRequestService userRequestService;

    private final ImageFactory imageFactory;

    private final VerificationTokenService verificationTokenService;

    /**
     * Функция поиска пользователя по индификатору
     *
     * @param id индификатор пользователя
     * @return найденный пользователь из базы данных
     */
    public User findUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    /**
     * Функция поиска пользователя по имени
     *
     * @param username имя пользователя
     * @return пользователь
     * @throws UserNotFoundException возникает при ненахождении пользователя
     */
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    /**
     * Функция ищет пользователя в базе данных
     *
     * @param username имя пользователя
     * @return результат поиска пользователя
     */
    public boolean userIsExistByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Функция ищет пользователя в базе данных
     *
     * @param email email пользователя
     * @return результат поиска пользователя
     */
    public boolean userIsExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Функция поиска всех пользователей из базы данных
     *
     * @return все пользователи из базы данных
     */
    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Функция добавления пользователя в базу данных
     *
     * @param username имя пользователя
     * @param email почта пользователя
     * @param password пароль пользователя
     */
    public User saveNewUser(String username, String email, String password) throws UserAlreadyExistException, RoleNotFoundException, IOException {

        if (userRepository.findByUsername(username).isPresent())
            throw new UserAlreadyExistException("Такой пользователь уже существует");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDate(new Date());
        user.setRoles(Collections.singleton(roleService.getRoleByName("ROLE_USER")));
        user.setEnabled(false);
        user.setCredentials_expired(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setAvatar(
                imageFactory.getImage(new File(ResourceUtils.getFile("classpath:static/img/user.png").getPath()))
        );

        return userRepository.save(user);
    }

    public void saveUser(User user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UserAlreadyExistException("Такой пользователь уже существует");
        userRepository.save(user);
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
            throw new UserNotFoundException("Пользователь не найден");
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
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        user.switchLock();
        userRepository.save(user);
    }

    @Transactional
    public void activateUserByUsername(String username) throws UserNotFoundException, UserAlreadyActivatedException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if(user.isEnabled())
            throw new UserAlreadyActivatedException("Данный пользователь уже активирован");

        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Метод удаляет пользователя из базы данных
     * @param id уникальный индификатор пользователя
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     */
    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            try {
                verificationTokenService.deleteByUser(user.get());
                decisionService.deleteAllDecisionByAuthor(user.get());
                userRequestService.deleteAllByUser(user.get());
            } catch (UserRequestNotFoundException | VerificationTokenNotFoundException ignored) {}
            //Если обращений, токенов верификации, частых вопросов не нашлось, то удалять и нечего
            userRepository.deleteById(id);
        }
    }

    /**
     * Метод удаляет пользователя из базы данных
     * @param username имя пользователя
     * @throws UserNotFoundException возникает при ненахождении пользователя в базе данных
     */
    @Transactional
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            try {
                verificationTokenService.deleteByUser(user.get());
                decisionService.deleteAllDecisionByAuthor(user.get());
                userRequestService.deleteAllByUser(user.get());
            } catch (UserRequestNotFoundException | VerificationTokenNotFoundException ignored) {}
            //Если обращений, токенов верификации, частых вопросов не нашлось, то удалять и нечего
            userRepository.deleteUserByUsername(username);
        }
    }

    /**
     * Функция поиска пользователя по имени
     * @param s имя пользователя
     * @return найденный пользователь
     * @throws UsernameNotFoundException возникает при ненахождения пользователя в базе данных
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
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
}
