package com.terflo.helpdesk.model.services.interfaces;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.exceptions.RoleAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;

import java.util.Set;

public interface RoleService {

    Role saveRole(Role role) throws RoleAlreadyExistException;

    void deleteRole(Role role) throws RoleNotFoundException;

    void deleteRoleByName(String roleName) throws RoleNotFoundException;

    Role getRoleByName(String roleName) throws RoleNotFoundException;

    Set<Role> findAll();

}
