package com.example.NewBuildingFinance.repository.chat;

import com.example.NewBuildingFinance.entities.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderByCreatedDateTimeAsc(Long chatId);
}
