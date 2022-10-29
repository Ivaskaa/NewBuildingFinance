package com.example.NewBuildingFinance.dto.auth;

import com.example.NewBuildingFinance.entities.auth.Permission;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;

    public Permission build(){
        Permission permission = new Permission();
        permission.setId(id);
        return permission;
    }
}
