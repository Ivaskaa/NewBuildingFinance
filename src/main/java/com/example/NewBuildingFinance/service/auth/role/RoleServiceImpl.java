package com.example.NewBuildingFinance.service.auth.role;

import com.example.NewBuildingFinance.dto.auth.RoleDto;
import com.example.NewBuildingFinance.entities.auth.Role;
import com.example.NewBuildingFinance.repository.auth.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        log.info("get roles");
        List<Role> roles = roleRepository.findAll();
        roles.removeIf(role -> role.getId().equals(1L));
        log.info("success");
        return roles;
    }

    @Override
    public Role save(Role role) {
        log.info("save role: {}", role);
        roleRepository.save(role);
        log.info("success");
        return role;
    }

    @Override
    public void updateRoles(List<RoleDto> rolesForm) {
        log.info("update roles: {}", rolesForm);
        for(RoleDto roleDto : rolesForm) {
            Role role = roleRepository.findById(roleDto.getId()).orElseThrow();
            role.setPermissions(roleDto.build().getPermissions());
            roleRepository.save(role);
        }
        log.info("success");
    }

    @Override
    public void deleteById(Long id) {
        if(id != 1) {
            log.info("delete role by id: {}", id);
            roleRepository.deleteById(id);
            log.info("success");
        }
    }

    @Override
    public boolean checkName(String name) {
        Role checkedRole = roleRepository.findRoleByName(name).orElse(null);
        return checkedRole != null;
    }
}
