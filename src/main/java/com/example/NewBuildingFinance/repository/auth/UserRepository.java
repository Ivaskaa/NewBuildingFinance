package com.example.NewBuildingFinance.repository.auth;

import com.example.NewBuildingFinance.entities.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    User findByMainAdminTrue();
    Page<User> findAllByDeletedFalseAndMainAdminFalse(Pageable pageable);
    @Query("SELECT u from User u JOIN u.role.permissions p where p.name = 'BUYERS' and u.active = true and u.deleted = false or u.id = :id group by u.id")
    List<User> findManagers(@Param("id") Long userId);

    @Modifying
    @Query(value = "update newbuildingfinance.users set users.deleted = true, users.active = false where users.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);

    @Query("SELECT u FROM User u JOIN u.role.permissions p WHERE p.name = :permissionName AND u.active = true")
    List<User> findUsersWithPermission(@Param("permissionName") String permissionName);
}