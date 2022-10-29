package com.example.NewBuildingFinance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class ScheduleService {
    private final RestTemplateService restTemplateService;
    private final CurrencyService currencyService;

    @Scheduled(cron = "0 0 8 * * *") // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
    public void getCurrencyFromApi() throws JsonProcessingException {
        currencyService.saveCurrency(restTemplateService.getCurrency());
    }
}
