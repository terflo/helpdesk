package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.exceptions.UserAlreadyActivatedException;
import com.terflo.helpdesk.model.exceptions.UserAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.UserNotFoundException;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User findUserById(Long id) throws UserNotFoundException;

    User findUserByUsername(String username) throws UserNotFoundException;

    User findUserByEmail(String email) throws UserNotFoundException;

    User saveUser(User user) throws UserAlreadyExistException;

    void deleteUserById(Long id) throws UserNotFoundException;

    void deleteUserByUsername(String username) throws UserNotFoundException;

    void deleteUser(User user) throws UserNotFoundException;

    void deleteUser(List<User> users) throws UserNotFoundException;

    boolean userIsExistByUsername(String username);

    boolean userIsExistByEmail(String email);

    List<User> getAllUsers();

    List<String> getActiveUsernamesFromSessionRegistry();

    void addRoleToUser(Long id, Role role) throws UserNotFoundException;

    void deleteRoleToUser(Long id, Role role) throws UserNotFoundException;

    void updateUser(User user) throws UserNotFoundException;

    void switchLockUserById(Long id) throws UserNotFoundException;

    void activateUserByUsername(String username) throws UserNotFoundException, UserAlreadyActivatedException;

}
