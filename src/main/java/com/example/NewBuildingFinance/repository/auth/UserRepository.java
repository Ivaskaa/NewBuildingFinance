package com.example.NewBuildingFinance.repository.auth;

import com.example.NewBuildingFinance.entities.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    @Query("SELECT u from User u JOIN u.role.permissions p where p.name = 'BUYERS'")
    List<User> findManagers();
}