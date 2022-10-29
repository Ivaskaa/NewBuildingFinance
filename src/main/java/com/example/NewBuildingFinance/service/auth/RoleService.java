package com.example.NewBuildingFinance.service.auth;

import com.example.NewBuildingFinance.dto.auth.RoleDto;
import com.example.NewBuildingFinance.entities.auth.Role;
import com.example.NewBuildingFinance.repository.auth.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        log.info("get roles");
        List<Role> roles = roleRepository.findAll();
        roleRepository.findById(1L).ifPresent(roles::remove);
        log.info("success");
        return roles;
    }

    public Role save(Role role) {
        log.info("save role: {}", role);
        roleRepository.save(role);
        log.info("success");
        return role;
    }


    public List<RoleDto> update(List<RoleDto> rolesForm) {
        log.info("update roles: {}", rolesForm);
        for(RoleDto roleDto : rolesForm) {
            Role role = roleRepository.findById(roleDto.getId()).orElseThrow();
            role.setPermissions(roleDto.build().getPermissions());
            roleRepository.save(role);
        }
        log.info("success");
        return rolesForm;
    }

    public void deleteById(Long id) {
        if(id != 1) {
            log.info("delete role by id: {}", id);
            roleRepository.deleteById(id);
            log.info("success");
        }
    }

    public boolean checkName(Role role) {
        Role checkedRole = roleRepository.findRoleByName(role.getName()).orElse(null);
        return checkedRole != null;
    }


}
