package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.*;
import com.terflo.helpdesk.model.factories.ImageFactory;
import com.terflo.helpdesk.model.repositories.UserRepository;
import com.terflo.helpdesk.model.services.interfaces.UserService;
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
 * @version 1.3
 * Сервис для работы с пользователями
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final DecisionServiceImpl decisionServiceImpl;

    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий ролей
     */
    private final RoleServiceImpl roleServiceImpl;

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
    private final UserRequestServiceImpl userRequestServiceImpl;

    private final ImageFactory imageFactory;

    private final ImageServiceImpl imageServiceImpl;

    private final VerificationTokenServiceImpl verificationTokenServiceImpl;

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

    /**
     * Функция добавления пользователя в базу данных
     *
     * @param username имя пользователя
     * @param email почта пользователя
     * @param password пароль пользователя
     */
    public User saveNewUser(String username, String email, String password) throws UserAlreadyExistException, RoleNotFoundException, IOException {

        if (userRepository.findByUsername(username).isPresent())
            throw new UserAlreadyExistException(
                    String.format("Пользователь %s уже существует", username)
            );

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDate(new Date());
        user.setRoles(Collections.singleton(roleServiceImpl.getRoleByName("ROLE_USER")));
        user.setEnabled(false);
        user.setCredentials_expired(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setAvatar(
                imageFactory.getImage(
                        new File(ResourceUtils.getFile("classpath:static/img/user.png").getPath())
                )
        );

        return userRepository.save(user);
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
    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь #%s не найден", id));
        } else {
            try {
                verificationTokenServiceImpl.deleteByUser(user.get());
                decisionServiceImpl.deleteAllDecisionByAuthor(user.get());
                userRequestServiceImpl.deleteAllByUser(user.get());
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
    @Override
    @Transactional
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь %s не найден", username));
        } else {
            try {
                verificationTokenServiceImpl.deleteByUser(user.get());
                decisionServiceImpl.deleteAllDecisionByAuthor(user.get());
                imageServiceImpl.deleteImage(user.get().getAvatar());
                userRequestServiceImpl.deleteAllByUser(user.get());
            } catch (UserRequestNotFoundException | VerificationTokenNotFoundException | ImageNotFoundException ignored) {}
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
        return userRepository
                .findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь %s не найден", s)));
    }
}
