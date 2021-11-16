package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    void deleteUserByUsername(String username);

}
