package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.setting.Setting;
import com.example.NewBuildingFinance.service.auth.user.UserService;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyService;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.setting.SettingService;
import com.example.NewBuildingFinance.service.setting.SettingServiceImpl;
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
@RequestMapping("/settings/settings")
public class SettingsController {
    private final InternalCurrencyService internalCurrencyService;
    private final UserService userService;
    private final SettingService settingService;

    private final ObjectMapper mapper;

    @GetMapping()
    public String settings(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("user", user);
        return "settings/settings";
    }

    @GetMapping("/getSettings")
    @ResponseBody
    public String getSettings() throws JsonProcessingException {
        return mapper.writeValueAsString(settingService.getSettings());
    }

    @PostMapping("/updateSettings")
    @ResponseBody
    public String updateSettings(
            @Valid @RequestBody Setting setting,
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
        settingService.updateSettings(setting);
        return mapper.writeValueAsString(null);
    }
}
