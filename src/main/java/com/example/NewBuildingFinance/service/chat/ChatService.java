package com.example.NewBuildingFinance.service.chat;

import com.example.NewBuildingFinance.dto.chat.ChatResponse;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.chat.Chat;
import com.example.NewBuildingFinance.repository.auth.UserRepository;
import com.example.NewBuildingFinance.repository.chat.ChatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    public List<ChatResponse> getChatsForUser(Long userId) {
        return chatRepository.findAllByUserId(userId).stream()
                .map(chat -> ChatResponse.builder()
                        .id(chat.getId())
                        .name(chat.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<User> getAvailableUsersForChat(User currentUser) {
        List<User> allChatUsers = userRepository.findUsersWithPermission("CHATS");
        List<Chat> existingChats = chatRepository.findAllByUsersContaining(currentUser);

        Set<Long> existingChatUserIds = existingChats.stream()
                .flatMap(chat -> chat.getUsers().stream())
                .map(User::getId)
                .collect(Collectors.toSet());

        return allChatUsers.stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .filter(user -> !existingChatUserIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Chat createChatWithUser(User currentUser, Long otherUserId, String chatName) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = new Chat();
        chat.setName(chatName);
        chat.setUsers(new HashSet<>(Arrays.asList(currentUser, otherUser)));

        try {
            // Генеруємо AES-ключ для чату
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey aesKey = keyGen.generateKey();

            // Шифруємо AES-ключ для кожного учасника
            Map<Long, String> encryptedKeys = new HashMap<>();
            String encryptedForCurrent = encryptionService.encryptAESKeyForUser(aesKey, currentUser.getPublicKey());
            String encryptedForOther = encryptionService.encryptAESKeyForUser(aesKey, otherUser.getPublicKey());
            encryptedKeys.put(currentUser.getId(), encryptedForCurrent);
            encryptedKeys.put(otherUser.getId(), encryptedForOther);

            String json = objectMapper.writeValueAsString(encryptedKeys);
            chat.setEncryptedAESKeys(json);
        } catch(Exception e) {
            throw new RuntimeException("Encryption error", e);
        }

        return chatRepository.save(chat);
    }

    public String getAesKey(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        try {
            // Парсимо JSON, де ключем є ID користувача, а значення – зашифрований AES-ключ
            Map<Long, String> encryptedAESKeysMap = new ObjectMapper().readValue(
                    chat.getEncryptedAESKeys(),
                    new TypeReference<Map<Long, String>>() {}
            );
            String encryptedAESKey = encryptedAESKeysMap.get(userId);
            if (encryptedAESKey == null) {
                throw new RuntimeException("AES key for user " + userId + " not found");
            }
            return encryptedAESKey;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing AES keys", e);
        }
    }
}
