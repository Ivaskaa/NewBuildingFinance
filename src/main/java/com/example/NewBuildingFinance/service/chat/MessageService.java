package com.example.NewBuildingFinance.service.chat;

import com.example.NewBuildingFinance.dto.chat.MessageRequest;
import com.example.NewBuildingFinance.dto.chat.MessageResponse;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.chat.Chat;
import com.example.NewBuildingFinance.entities.chat.Message;
import com.example.NewBuildingFinance.repository.chat.ChatRepository;
import com.example.NewBuildingFinance.repository.chat.MessageRepository;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public MessageResponse sendMessage(Long chatId, MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());

        if (!chat.getUsers().contains(user)) {
            throw new RuntimeException("User not in chat");
        }

        Message messageToSave = new Message();
        messageToSave.setChat(chat);
        messageToSave.setUser(user);
        messageToSave.setText(messageRequest.getEncryptedText());
        messageToSave.setIv(messageRequest.getIv());
        Message savedMessage = messageRepository.save(messageToSave);

        MessageResponse response = MessageResponse.builder()
                .userId(savedMessage.getUser().getId())
                .senderName(savedMessage.getUser().getName())
                .content(savedMessage.getText()) // Зашифрований текст
                .iv(savedMessage.getIv())
                .createdAt(savedMessage.getCreatedDateTime())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + chatId, response);
        return response;
    }

    public List<MessageResponse> getMessages(Long chatId) {
        // Повертаємо зашифровані повідомлення, фронтенд сам розшифрує
        return messageRepository.findByChatIdOrderByCreatedDateTimeAsc(chatId)
                .stream()
                .map(message -> MessageResponse.builder()
                        .userId(message.getUser().getId())
                        .senderName(message.getUser().getName())
                        .content(message.getText())
                        .iv(message.getIv())
                        .createdAt(message.getCreatedDateTime())
                        .build()).collect(Collectors.toList());
    }
}
