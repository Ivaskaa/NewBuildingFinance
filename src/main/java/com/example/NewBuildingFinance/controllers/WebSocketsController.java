package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.CurrencyDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketsController {

    @MessageMapping("/currency")    // before that must input /app (check WebSocketConfig)
    @SendTo("/topic/currency")
    public CurrencyDto greeting(
            CurrencyDto currencyDto
    ) throws Exception {
        return currencyDto;
    }

    @MessageMapping("/notifications")  // before that must input /app (check WebSocketConfig)
    @SendTo("/topic/notifications")
    public String notifications() throws Exception {
        return "success";
    }


    @MessageMapping("/hello")  // before that must input /app (check WebSocketConfig)
    @SendTo("/topic/hello")
    public String hello(String string) {
        return string;
    }

    @GetMapping("/websockets")
    public String agencies(){
        return "websockets/index";
    }
}