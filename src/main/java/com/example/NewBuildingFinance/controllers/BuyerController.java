package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.BuyerDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.service.AgencyService;
import com.example.NewBuildingFinance.service.BuyerService;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.RealtorService;
import com.example.NewBuildingFinance.service.auth.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/buyers")
public class BuyerController {
    private final InternalCurrencyService currencyService;
    private final BuyerService buyerService;
    private final RealtorService realtorService;
    private final AgencyService agencyService;
    private final UserService userService;
    private final ObjectMapper mapper;

    @GetMapping()
    public String buyers(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "buyer/buyers";
    }

    @GetMapping("/getBuyers")
    @ResponseBody
    public String getBuyers(
            Integer page,
            Integer size,
            String field,
            String direction
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(buyerService.findSortingPage(
                page, size, field, direction));
    }

    @PostMapping("/addBuyer")
    @ResponseBody
    public String addBuyer(
            @Valid @RequestBody BuyerDto buyerDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        buyerService.save(buyerDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateBuyer")
    @ResponseBody
    public String updateBuyer(
            @Valid @RequestBody BuyerDto buyerDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        buyerService.update(buyerDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerService.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @PostMapping("/deleteBuyerById")
    @ResponseBody
    public String deleteObjectById(
            Long id
    ) throws JsonProcessingException {
        buyerService.deleteById(id);
        return mapper.writeValueAsString("success");
    }

    @GetMapping("/getAllAgencies")
    @ResponseBody
    public String getAllAgencies() throws JsonProcessingException {
        return mapper.writeValueAsString(agencyService.findAll());
    }

    @GetMapping("/getRealtorsByAgenciesId")
    @ResponseBody
    public String getRealtorsByAgenciesId(Long id) throws JsonProcessingException {
        return mapper.writeValueAsString(realtorService.findAllByAgencyId(id));
    }

    @GetMapping("/getAllManagers")
    @ResponseBody
    public String getAllManagers() throws JsonProcessingException {
        return mapper.writeValueAsString(userService.findManagers());
    }
}
