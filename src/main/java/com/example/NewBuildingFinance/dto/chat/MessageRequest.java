package com.example.NewBuildingFinance.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotBlank(message = "Message text cannot be blank")
    private String encryptedText;
    @NotBlank(message = "Message text cannot be blank")
    private String iv;
}
