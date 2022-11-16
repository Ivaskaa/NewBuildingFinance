package com.example.NewBuildingFinance.service.auth.role;

import com.example.NewBuildingFinance.dto.auth.RoleDto;
import com.example.NewBuildingFinance.entities.auth.Role;

import java.util.List;

public interface RoleService {
    /**
     * find all roles from database except the role from id 1
     * @return list of roles
     */
    List<Role> findAll();

    /**
     * save new role without permissions
     * @param role role object
     * @return role after save
     */
    Role save(Role role);

    /**
     * role permissions permissions
     * @param rolesForm role object
     */
    void updateRoles(List<RoleDto> rolesForm);

    /**
     * delete role by id
     * @param id delete id
     */
    void deleteById(Long id);

    /**
     * check unique role name
     * @param name role name
     * @return if name is exist return true
     */
    boolean checkName(String name);
}
