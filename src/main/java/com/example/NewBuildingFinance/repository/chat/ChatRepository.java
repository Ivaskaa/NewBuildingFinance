package com.example.NewBuildingFinance.repository.chat;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findAllByUserId(Long userId);

    List<Chat> findAllByUsersContaining(User currentUser);
}
