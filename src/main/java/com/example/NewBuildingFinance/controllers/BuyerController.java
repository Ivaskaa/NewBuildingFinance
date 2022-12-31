package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.buyer.BuyerSaveDto;
import com.example.NewBuildingFinance.dto.buyer.BuyerUploadDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.service.agency.AgencyService;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.buyer.BuyerService;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import com.example.NewBuildingFinance.service.contract.ContractService;
import com.example.NewBuildingFinance.service.contract.ContractServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyService;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.realtor.RealtorService;
import com.example.NewBuildingFinance.service.realtor.RealtorServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
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
    private final InternalCurrencyService internalCurrencyService;
    private final BuyerService buyerService;
    private final ContractService contractService;
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
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("user", user);
        return "buyer/buyers";
    }

    @GetMapping("/contracts/{buyerId}")
    public String buyerContracts(
            @PathVariable Long buyerId,
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
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
        return mapper.writeValueAsString(contractService.findSortingPageByBuyerId(
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
        return mapper.writeValueAsString(buyerService.findSortingPage(
                page, size, field, direction));
    }

    @PostMapping("/addBuyer")
    @ResponseBody
    public String addBuyer(
            @Valid @RequestBody BuyerSaveDto buyerSaveDto,
            BindingResult bindingResult
    ) throws IOException, StripeException {
        //validation
        buyerService.documentValidation(bindingResult, buyerSaveDto);
        buyerService.phoneValidation(bindingResult, buyerSaveDto.getPhone());
        if(!bindingResult.hasErrors()){
            buyerService.emailValidation(bindingResult, buyerSaveDto.getEmail(), buyerSaveDto.getId());
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        buyerService.save(buyerSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateBuyer")
    @ResponseBody
    public String updateBuyer(
            @Valid @RequestBody BuyerSaveDto buyerSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        buyerService.documentValidation(bindingResult, buyerSaveDto);
        buyerService.phoneValidation(bindingResult, buyerSaveDto.getPhone());
        if(!bindingResult.hasErrors()){
            buyerService.emailValidation(bindingResult, buyerSaveDto.getEmail(), buyerSaveDto.getId());
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        buyerService.update(buyerSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        BuyerUploadDto buyerUploadDto = buyerService.findById(id).buildUploadDto();
        return mapper.writeValueAsString(buyerUploadDto);
    }

    @PostMapping("/deleteBuyerById")
    @ResponseBody
    public String deleteObjectById(
            Long id
    ) throws JsonProcessingException {
        buyerService.deleteById(id);
        return mapper.writeValueAsString("success");
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

    @GetMapping("/getAllAgencies")
    @ResponseBody
    public String getAllAgencies(Long agencyId) throws JsonProcessingException {
        return mapper.writeValueAsString(agencyService.findAllByDeletedFalse(agencyId));
    }

    @GetMapping("/getRealtorsByAgenciesId")
    @ResponseBody
    public String getRealtorsByAgenciesId(
            Long agencyId,
            Long realtorId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(
                realtorService.findAllByAgencyIdOrRealtorId(agencyId, realtorId));
    }

    @GetMapping("/getAllManagers")
    @ResponseBody
    public String getAllManagers(Long managerId) throws JsonProcessingException {
        return mapper.writeValueAsString(userService.findManagers(managerId));
    }
}
