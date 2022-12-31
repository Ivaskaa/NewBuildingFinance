package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsSearchDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyService;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectService;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.example.NewBuildingFinance.service.statistic.StatisticService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/statistics")
public class StatisticController {
    private final StatisticService statisticService;
    private final InternalCurrencyService internalCurrencyService;
    private final UserService userService;
    private final ObjectService objectService;

    private final ObjectMapper mapper;

    @GetMapping("/plan-fact")
    public String planFact(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectService.findAll());
        model.addAttribute("currencies", internalCurrencyService.findAll());
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

    @GetMapping("/flats")
    public String flats(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId());
        model.addAttribute("objects", objectService.findAll());
        List<Pair<StatusFlat, String>> list = new ArrayList<>();
        for(StatusFlat statusObject : StatusFlat.values()){
            list.add(Pair.of(statusObject, statusObject.getValue()));
        }
        model.addAttribute("status", list);
        model.addAttribute("currencies", internalCurrencyService.findAll());

        // page model
        model.addAttribute("statistic", statisticService.getFlatBoxes());
        return "statistics/statisticFlats";
    }

    @GetMapping("/getFlatsStatistic")
    @ResponseBody
    public String getFlatsStatistic(
            StatisticFlatsSearchDto searchDto
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(
                statisticService.getFlatPaymentStatistic(searchDto));
    }

    @GetMapping("/getFlatPaymentsForBarChart")
    @ResponseBody
    public String getFlatPaymentsForBarChart(
            Long flatId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(
                statisticService.getFlatPaymentsByFlatId(flatId));
    }
    // others

    @GetMapping ("/getUserPermissionsById")
    @ResponseBody
    public String getUserPermissionsById(
            Long id
    ) throws JsonProcessingException {
        List<String> permissions = userService.getUserPermissionsById(id);
        return mapper.writeValueAsString(permissions);
    }



}
