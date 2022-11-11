package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class SettingsController {
    private final NotificationService notificationService;
    private final ObjectMapper mapper;

    @GetMapping( "/getNotifications" )
    @ResponseBody
    public String getNotifications() throws JsonProcessingException {
        List<Notification> notifications = notificationService.findAll();
        return mapper.writeValueAsString(notifications);
    }

}