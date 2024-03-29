package com.example.NewBuildingFinance.repository.auth;

import com.example.NewBuildingFinance.entities.auth.Role;
import com.example.NewBuildingFinance.entities.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByName(String name);

    Role findByMainRoleTrue();
    List<Role> findAll();
    List<Role> findAllByMainRoleFalse();
}
