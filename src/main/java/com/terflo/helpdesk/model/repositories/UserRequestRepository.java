package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import com.terflo.helpdesk.model.entity.UserRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRequestRepository extends CrudRepository<UserRequest, Long> {

    List<UserRequest> findAllByUser(User user);

    List<UserRequest> findAllByOperator(User Operator);

    List<UserRequest> findAllByOperatorOrderByStatus(User operator);

    void deleteAllByUser(User user);
}
