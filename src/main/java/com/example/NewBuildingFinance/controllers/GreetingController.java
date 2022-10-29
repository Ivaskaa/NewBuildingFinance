package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.CurrencyDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public CurrencyDto greeting(
            CurrencyDto currencyDto
    ) throws Exception {
        System.out.println(currencyDto);
        return currencyDto;
    }
}