package com.example.NewBuildingFinance.service.schedule;

import com.example.NewBuildingFinance.service.currency.CurrencyServiceImpl;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import com.example.NewBuildingFinance.service.restTemplate.RestTemplateServiceImpl;
import com.example.NewBuildingFinance.service.statistic.StatisticServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final RestTemplateServiceImpl restTemplateServiceImpl;
    private final NotificationServiceImpl notificationServiceImpl;
    private final CurrencyServiceImpl currencyServiceImpl;

//    @Override
//    @Scheduled(cron = "1/12 * * * * *") // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
//    public void getCurrencyFromApi() throws Exception {
//        currencyServiceImpl.saveCurrency(restTemplateServiceImpl.getCurrency());
//    }

    @Override
    @Scheduled(cron = "0 0 8 * * *") // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
    public void getCurrencyFromApi() throws Exception {
        currencyServiceImpl.saveCurrency(restTemplateServiceImpl.getCurrency());
    }

    @Override
    @Scheduled(cron = "0 0 1 * * *")
    public void updateNotifications() {
        notificationServiceImpl.updateNotifications();
    }

//    @Scheduled(cron = "1/12 * * * * *")
//    public void main() {
//        Date date1 = new Date(1000000000L);
//        Date date2 = new Date(10000000000L);
//        statisticService.getMainMonthlyStatistic(1L, date1, date2);
//    }
}
