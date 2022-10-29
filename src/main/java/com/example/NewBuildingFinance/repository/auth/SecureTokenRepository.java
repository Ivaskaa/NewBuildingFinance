package com.example.NewBuildingFinance.repository.auth;

import com.example.NewBuildingFinance.entities.auth.SecureToken;
import com.example.NewBuildingFinance.entities.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecureTokenRepository extends JpaRepository<SecureToken, Long>{
    SecureToken findByToken(String token);
    Long removeByToken(String token);
}
