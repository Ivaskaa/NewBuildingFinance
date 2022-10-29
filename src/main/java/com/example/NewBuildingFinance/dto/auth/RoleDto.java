package com.example.NewBuildingFinance.dto.auth;

import com.example.NewBuildingFinance.entities.auth.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RoleDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String name;
    private Set<PermissionDto> permissions;

    public Role build(){
        Role role = new Role();
        role.setId(id);;
        role.setName(name);
        if(permissions == null) {
            role.setPermissions(new HashSet<>());
        } else {
            role.setPermissions(permissions.stream().map(PermissionDto::build).collect(Collectors.toSet()));
        }
        return role;
    }
}
