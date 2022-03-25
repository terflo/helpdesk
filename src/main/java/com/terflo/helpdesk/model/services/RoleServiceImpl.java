package com.terflo.helpdesk.model.services;

import com.terflo.helpdesk.model.entity.Role;
import com.terflo.helpdesk.model.exceptions.RoleAlreadyExistException;
import com.terflo.helpdesk.model.exceptions.RoleNotFoundException;
import com.terflo.helpdesk.model.repositories.RoleRepository;
import com.terflo.helpdesk.model.services.interfaces.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role saveRole(Role role) throws RoleAlreadyExistException {
        if(roleRepository.findByName(role.getName()).isPresent())
            throw new RoleAlreadyExistException("Роль с именем " + role.getName() + " уже существует");
        else
            return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Role role) throws RoleNotFoundException {
        if(!roleRepository.findByName(role.getName()).isPresent())
            throw new RoleNotFoundException("Роль с имененм " + role.getName() + " не найдена");
        else
            roleRepository.delete(role);
    }

    @Override
    @Transactional
    public void deleteRoleByName(String roleName) throws RoleNotFoundException {
        if(!roleRepository.findByName(roleName).isPresent())
            throw new RoleNotFoundException("Роль с имененм " + roleName + " не найдена");
        else
            roleRepository.deleteByName(roleName);
    }

    @Override
    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Роль с имененм " + roleName + " не найдена"));
    }

    @Override
    public Set<Role> findAll() {
        return StreamSupport
                .stream(roleRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }
}
