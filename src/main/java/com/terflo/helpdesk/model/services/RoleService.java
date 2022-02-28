package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.exceptions.RoleAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void saveRole(Role role) throws RoleAlreadyExistException {
        if(roleRepository.findByName(role.getName()).isPresent())
            throw new RoleAlreadyExistException("Роль с именем " + role.getName() + " уже существует");
        else
            roleRepository.save(role);
    }

    public void saveRole(String roleName) throws RoleAlreadyExistException {
        if(roleRepository.findByName(roleName).isPresent())
            throw new RoleAlreadyExistException("Роль с именем " + roleName + " уже существует");
        else {
            roleRepository.save(new Role(null, roleName));
        }
    }

    public void deleteRole(Role role) throws RoleNotFoundException {
        if(!roleRepository.findByName(role.getName()).isPresent())
            throw new RoleNotFoundException("Роль с имененм " + role.getName() + " не найдена");
        else
            roleRepository.delete(role);
    }

    public void deleteRole(String roleName) throws RoleNotFoundException {
        if(!roleRepository.findByName(roleName).isPresent())
            throw new RoleNotFoundException("Роль с имененм " + roleName + " не найдена");
        else
            roleRepository.deleteByName(roleName);
    }

    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Роль с имененм " + roleName + " не найдена"));
    }

    public Set<Role> findAll() {
        return StreamSupport
                .stream(roleRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }
}
