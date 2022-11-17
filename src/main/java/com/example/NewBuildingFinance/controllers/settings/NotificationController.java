package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class NotificationController {
    private final NotificationServiceImpl notificationServiceImpl;
    private final ObjectMapper mapper;

    @GetMapping("/notification/{id}")
    public String notification(
            @PathVariable(required = false) Long id
    ) throws ParseException {
        Notification notification = notificationServiceImpl.setReviewed(id);
        return "redirect:" + notification.getUrl();
    }

    @GetMapping( "/getNotifications" )
    @ResponseBody
    public String getNotifications() throws JsonProcessingException {
        List<Notification> notifications = notificationServiceImpl.findAll();
        return mapper.writeValueAsString(notifications);
    }
}
