package com.example.NewBuildingFinance.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long userId;
    private String senderName;
    private String content;
    private String iv;
    private LocalDateTime createdAt;
}
