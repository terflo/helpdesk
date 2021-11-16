package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

}
