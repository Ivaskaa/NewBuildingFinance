package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.dto.CurrencyDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.service.CurrencyService;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
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
@RequestMapping("/settings")
public class CurrencyController {
    private final InternalCurrencyService internalCurrencyService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final ObjectMapper mapper;

    @GetMapping("/currency")
    public String currency(
            Model model
    ) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("currency", currencyService.getAll());
        model.addAttribute("user", user);
        return "currency";
    }

    @GetMapping("/getCurrencies")
    @ResponseBody
    public String getCurrencies() throws JsonProcessingException {
        return mapper.writeValueAsString(internalCurrencyService.findAll());
    }

    @PostMapping("/updateCurrency")
    @ResponseBody
    public String updateObject(
            @Valid @RequestBody CurrencyDto currencyDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (internalCurrencyService.checkPrice(currencyDto.getPrice())){
            bindingResult.addError(new FieldError("currencyDto", "price", "Must be double in format xx.xx"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        internalCurrencyService.update(currencyDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getCurrencyById")
    @ResponseBody
    public String getCurrencyById(
            Long id
    ) throws JsonProcessingException {
        InternalCurrency currency = internalCurrencyService.findById(id);
        return mapper.writeValueAsString(currency);
    }
}
