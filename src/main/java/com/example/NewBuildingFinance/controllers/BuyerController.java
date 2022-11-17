package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.buyer.BuyerDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import com.example.NewBuildingFinance.service.contract.ContractServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.realtor.RealtorServiceImpl;
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
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/buyers")
public class BuyerController {
    private final InternalCurrencyServiceImpl internalCurrencyServiceImpl;
    private final BuyerServiceImpl buyerServiceImpl;
    private final ContractServiceImpl contractServiceImpl;
    private final RealtorServiceImpl realtorServiceImpl;
    private final AgencyServiceImpl agencyServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ObjectMapper mapper;

    @GetMapping()
    public String buyers(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());
        model.addAttribute("user", user);
        return "buyer/buyers";
    }

    @GetMapping("/contracts/{buyerId}")
    public String buyerContracts(
            @PathVariable Long buyerId,
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());
        model.addAttribute("userId", user.getId());
        model.addAttribute("buyerId", buyerId);
        model.addAttribute("user", user);
        return "buyer/contracts";
    }

    @GetMapping("/getContractsByBuyerId")
    @ResponseBody
    public String getContractsByBuyerId(
            Integer page,
            Integer size,
            String field,
            String direction,
            Long buyerId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(contractServiceImpl.findSortingPageByBuyerId(
                page, size, field, direction, buyerId));
    }

    @GetMapping("/getBuyers")
    @ResponseBody
    public String getBuyers(
            Integer page,
            Integer size,
            String field,
            String direction
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(buyerServiceImpl.findSortingPage(
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
        buyerServiceImpl.save(buyerDto.build());
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
        buyerServiceImpl.update(buyerDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerServiceImpl.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @PostMapping("/deleteBuyerById")
    @ResponseBody
    public String deleteObjectById(
            Long id
    ) throws JsonProcessingException {
        buyerServiceImpl.deleteById(id);
        return mapper.writeValueAsString("success");
    }

    // others

    @GetMapping ("/getUserPermissionsById")
    @ResponseBody
    public String getUserPermissionsById(
            Long id
    ) throws JsonProcessingException {
        List<String> permissions = userServiceImpl.getUserPermissionsById(id);
        return mapper.writeValueAsString(permissions);
    }

    @GetMapping("/getAllAgencies")
    @ResponseBody
    public String getAllAgencies() throws JsonProcessingException {
        return mapper.writeValueAsString(agencyServiceImpl.findAll());
    }

    @GetMapping("/getRealtorsByAgenciesId")
    @ResponseBody
    public String getRealtorsByAgenciesId(Long id) throws JsonProcessingException {
        return mapper.writeValueAsString(realtorServiceImpl.findAllByAgencyId(id));
    }

    @GetMapping("/getAllManagers")
    @ResponseBody
    public String getAllManagers() throws JsonProcessingException {
        return mapper.writeValueAsString(userServiceImpl.findManagers());
    }
}
