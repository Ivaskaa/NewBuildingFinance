package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.example.NewBuildingFinance.service.statistic.StatisticServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@AllArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {
    private final StatisticServiceImpl statisticService;

    private final InternalCurrencyServiceImpl currencyService;
    private final UserServiceImpl userServiceImpl;
    private final ObjectServiceImpl objectService;

    private final ObjectMapper mapper;

    @GetMapping("/plan-fact")
    public String planFact(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectService.findAll());
        model.addAttribute("currencies", currencyService.findAll());
        Pair<Date, Date> datePair = statisticService.getMinDateAndMaxDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String dateStart = format.format(datePair.getFirst());
        String dateFin = format.format(datePair.getSecond());
        model.addAttribute("dateStart", dateStart);
        model.addAttribute("dateFin", dateFin);
        model.addAttribute("user", user);
        return "statistics/statisticPlan-fact";
    }

    @GetMapping("/getPlanFactStatistics")
    @ResponseBody
    public String getPlanFactStatistics(
            Long objectId,
            String dateStart,
            String dateFin
    ) throws JsonProcessingException, ParseException {
        return mapper.writeValueAsString(statisticService.getMainMonthlyStatistic(
                objectId,
                dateStart,
                dateFin
        ));
    }

    @GetMapping("/flatPayments")
    public String flatPayments(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("objects", objectService.findAll());
        model.addAttribute("currencies", currencyService.findAll());

        // page model
        model.addAttribute("statistic", statisticService.getFlatBoxes());
        return "statistics/statisticFlatPayments";
    }

    @GetMapping("/getFlatPaymentsStatistic")
    public String getFlatPaymentsStatistic(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("objects", objectService.findAll());
        model.addAttribute("currencies", currencyService.findAll());

        // page model
        model.addAttribute("statistic", statisticService.getFlatBoxes());
        return "statistics/statisticFlatPayments";
    }

}
