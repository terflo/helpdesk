package com.terflo.helpdesk.model.repositories;

import com.terflo.helpdesk.model.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByName(String name);

}
