package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.agency.AgencyDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.ContractService;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
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
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/agencies")
public class AgencyController {
    private final InternalCurrencyService internalCurrencyService;
    private final UserServiceImpl userServiceImpl;
    private final ContractService contractService;
    private final AgencyServiceImpl agencyServiceImpl;

    private final ObjectMapper mapper;

    @GetMapping()
    public String agencies(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("user", user);
        return "agency/agencies";
    }

    @GetMapping("/agency/{id}")
    public String agency(
            @PathVariable(required = false) Long id,
            Model model
    ) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("agencyId", id);
        model.addAttribute("userId", user.getId());
        model.addAttribute("user", user);
        return "agency/agency";
    }

    @GetMapping("/getContractsByAgencyId")
    @ResponseBody
    public String getContractsByBuyerId(
            Integer page,
            Integer size,
            String field,
            String direction,
            Long agencyId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(contractService.findSortingPageByAgencyId(
                page, size, field, direction, agencyId));
    }

    @GetMapping("/getAgencies")
    @ResponseBody
    public String getAgencies(
            Integer page,
            Integer size,
            String field,
            String direction,

            String name,
            String director,
            String phone,
            String email,
            Integer count
    ) throws JsonProcessingException {
        if (page == 0){
            return null;
        }
        return mapper.writeValueAsString(agencyServiceImpl.findSortingAndSpecificationPage(
                page, size, field, direction,
                name,
                director,
                phone,
                email,
                count));
    }

    @PostMapping("/addAgency")
    @ResponseBody
    public String addAgency(
            @Valid @RequestBody AgencyDto agencyDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        System.out.println(agencyDto);
        if(agencyServiceImpl.checkAgencyName(agencyDto.getName())){
            bindingResult.addError(new FieldError("agencyDto", "name", "We already have agency with that name"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        Agency agency = agencyServiceImpl.save(agencyDto.build());
        return mapper.writeValueAsString(agency.getId());
    }

    @PostMapping("/updateAgency")
    @ResponseBody
    public String updateAgency(
            @Valid @RequestBody AgencyDto agencyDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        System.out.println(agencyDto);
        Agency agency = agencyServiceImpl.findById(agencyDto.getId());
        if(!agency.getName().equals(agencyDto.getName())){
            if(agencyServiceImpl.checkAgencyName(agencyDto.getName())){
                bindingResult.addError(new FieldError("agencyDto", "name", "We already have agency with that name"));
            }
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }
        //action
        agencyServiceImpl.update(agencyDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getAgencyById")
    @ResponseBody
    public String getAgencyById(
            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(agencyServiceImpl.findById(id));
    }

    @PostMapping("/deleteAgencyById")
    @ResponseBody
    public String deleteAgencyById(
            Long id
    ) throws JsonProcessingException {
        agencyServiceImpl.deleteById(id);
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
}
