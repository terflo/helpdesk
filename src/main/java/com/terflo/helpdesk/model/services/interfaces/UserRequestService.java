package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import com.terflo.helpdesk.model.entity.enums.RequestStatus;
import com.terflo.helpdesk.model.exceptions.UserRequestAlreadyHaveOperatorException;
import com.terflo.helpdesk.model.exceptions.UserRequestClosedException;
import com.terflo.helpdesk.model.exceptions.UserRequestNotFoundException;
import lombok.NonNull;

import java.util.List;

public interface UserRequestService {

    List<UserRequest> findAllUserRequestsByUser(@NonNull User user);

    List<UserRequest> findAllUserRequestsByOperator(User operator);

    List<UserRequest> findAll();

    UserRequest findUserRequestByID(@NonNull Long id) throws UserRequestNotFoundException;

    UserRequest saveUserRequest(@NonNull UserRequest userRequest);

    UserRequest acceptRequest(@NonNull Long id, @NonNull User operator) throws UserRequestNotFoundException, UserRequestAlreadyHaveOperatorException, UserRequestClosedException;

    UserRequest setStatusUserRequestByID(@NonNull Long id, @NonNull RequestStatus status) throws UserRequestNotFoundException;

    void deleteByID(@NonNull Long id) throws UserRequestNotFoundException;

    void deleteAllByUser(@NonNull User user) throws UserRequestNotFoundException;

}
