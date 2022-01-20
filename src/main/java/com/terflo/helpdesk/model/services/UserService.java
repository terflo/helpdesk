package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;
import com.terflo.helpdesk.model.repositories.RoleRepository;
import com.terflo.helpdesk.model.repositories.UserRepository;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Danil Krivoschiokov
 * @version 1.0
 * Сервис для работы с пользователями
 */
@Service
public class UserService implements UserDetailsService {

    /**
     * Репозиторий пользователей
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Репозиторий ролей
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Кодировщик паролей
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
     * @param user пользователь, которого нужно добавить
     */
    public void saveUser(User user) throws UserAlreadyExistException {

        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UserAlreadyExistException("Такой пользователь уже существует");

        user.setRoles(Collections.singleton(roleRepository.findByName("ROLE_USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setCredentials_expired(false);
        user.setExpired(false);
        user.setLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserByUsername(String username) throws UserNotFoundException {
        if (!userRepository.findByUsername(username).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteUserByUsername(username);
    }

    /**
     * Функция поиска пользователя по имени
     *
     * @param s имя пользователя
     * @return найденный пользователь
     * @throws UsernameNotFoundException возникает при ненахождения пользователя в базе данных
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
