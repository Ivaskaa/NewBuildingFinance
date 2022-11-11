package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String Notification(
            @PathVariable(required = false) Long id
    ) throws ParseException {
        Notification notification = notificationService.setReviewed(id);
        return "redirect:" + notification.getUrl();
    }
}
