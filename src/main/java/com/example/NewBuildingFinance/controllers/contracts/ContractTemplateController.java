package com.example.NewBuildingFinance.controllers.contracts;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import com.example.NewBuildingFinance.service.contractTemplate.ContractTemplateService;
import com.example.NewBuildingFinance.service.contractTemplate.ContractTemplateServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyService;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
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
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/contracts/templates")
public class ContractTemplateController {
    private final UserService userService;
    private final InternalCurrencyService internalCurrencyService;
    private final ContractTemplateService contractTemplateService;

    private final ObjectMapper mapper;

    @GetMapping()
    public String contractTemplates(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("user", user);
        return "contractTemplate";
    }

    @GetMapping("/getContractTemplates")
    @ResponseBody
    public String getContractTemplates(
            Long contractTemplateId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(contractTemplateService.findAllDeletedFalseOrById(contractTemplateId));
    }

    @PostMapping("/addContractTemplate")
    @ResponseBody
    public String addContractTemplate(
            @Valid @RequestBody ContractTemplate contractTemplate,
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
        contractTemplateService.save(contractTemplate);
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateContractTemplate")
    @ResponseBody
    public String updateBuyer(
            @Valid @RequestBody ContractTemplate contractTemplate,
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
        contractTemplateService.update(contractTemplate);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/setMainContractTemplate")
    @ResponseBody
    public String setMainContractTemplate(
            Long id
    ) throws JsonProcessingException {
        contractTemplateService.changeMain(id);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getContractTemplateById")
    @ResponseBody
    public String getContractTemplateById(
            Long id
    ) throws JsonProcessingException {
        ContractTemplate object = contractTemplateService.findById(id);
        return mapper.writeValueAsString(object);
    }

    @PostMapping("/deleteContractTemplateById")
    @ResponseBody
    public String deleteContractTemplateById(
            Long id
    ) throws JsonProcessingException {
        contractTemplateService.deleteById(id);
        return mapper.writeValueAsString(null);
    }
}
