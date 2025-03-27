package com.example.NewBuildingFinance.entities.chat;

import com.example.NewBuildingFinance.entities.auth.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    // Зашифрований текст повідомлення
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    // IV для AES шифрування
    @Column(columnDefinition = "TEXT")
    private String iv;
}
