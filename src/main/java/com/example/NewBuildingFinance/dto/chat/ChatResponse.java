package com.example.NewBuildingFinance.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private Long id;
    private String name;
}
