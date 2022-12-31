package com.example.NewBuildingFinance.repository.auth;

import com.example.NewBuildingFinance.entities.auth.Permission;
import com.example.NewBuildingFinance.entities.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository  extends JpaRepository<Permission, Long>{
    Permission findByName(String name);
}
