package com.example.NewBuildingFinance.service.schedule;

import com.example.NewBuildingFinance.service.currency.CurrencyService;
import com.example.NewBuildingFinance.service.notification.NotificationService;
import com.example.NewBuildingFinance.service.restTemplate.RestTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final RestTemplateService restTemplateService;
    private final NotificationService notificationService;
    private final CurrencyService currencyService;

    @Override
    @Scheduled(cron = "0 0 8 * * *") // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
    public void getCurrencyFromApi() throws Exception {
        currencyService.saveCurrency(restTemplateService.getCurrency());
    }

    @Override
    @Scheduled(cron = "0 0 1 * * *")
    public void updateNotifications() {
        notificationService.updateNotifications();
    }
}
