package com.example.NewBuildingFinance.controllers.chat;

import com.example.NewBuildingFinance.dto.chat.MessageRequest;
import com.example.NewBuildingFinance.dto.chat.MessageResponse;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.chat.Chat;
import com.example.NewBuildingFinance.repository.chat.ChatRepository;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import com.example.NewBuildingFinance.service.chat.ChatService;
import com.example.NewBuildingFinance.service.chat.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;
    private final ChatRepository chatRepository;

    private final ObjectMapper mapper;

    @GetMapping()
    public String chats(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId());
        model.addAttribute("privateKey", user.getPrivateKey());
        return "chat/chats";
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public String getChats(@PathVariable Long userId) throws JsonProcessingException {
        return mapper.writeValueAsString(chatService.getChatsForUser(userId));
    }

    @PostMapping("/{chatId}/message")
    @ResponseBody
    public String sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest messageRequest,
            BindingResult bindingResult
    ) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        MessageResponse message = messageService.sendMessage(chatId, messageRequest);
        return mapper.writeValueAsString(message);
    }

    @GetMapping("/{chatId}/messages")
    @ResponseBody
    public String getMessages(@PathVariable Long chatId) throws JsonProcessingException {
        return mapper.writeValueAsString(messageService.getMessages(chatId));
    }

    @GetMapping("/{chatId}/aes-key")
    @ResponseBody
    public String getChatAESKey(@PathVariable Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.loadUserByUsername(authentication.getName());
        try {
            Map<Long, String> encryptedKeys = mapper.readValue(chat.getEncryptedAESKeys(), new TypeReference<Map<Long, String>>() {});
            String encryptedAESKey = encryptedKeys.get(currentUser.getId());
            return mapper.writeValueAsString(encryptedAESKey);
        } catch(Exception e) {
            throw new RuntimeException("Error retrieving AES key", e);
        }
    }

    @GetMapping("/available-users")
    @ResponseBody
    public String getAvailableUsers() throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.loadUserByUsername(authentication.getName());
        List<User> availableUsers = chatService.getAvailableUsersForChat(currentUser);
        return mapper.writeValueAsString(availableUsers);
    }

    @PostMapping("/create-with-user/{otherUserId}")
    @ResponseBody
    public String createChatWithUser(@PathVariable Long otherUserId, @RequestParam String chatName) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.loadUserByUsername(authentication.getName());
        Chat chat = chatService.createChatWithUser(currentUser, otherUserId, chatName);
        return mapper.writeValueAsString(chat);
    }
}
