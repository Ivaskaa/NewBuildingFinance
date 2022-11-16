package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.dto.agency.AgencyDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.entities.setting.Setting;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.NotificationService;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
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
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/settings/settings")
public class SettingsController {
    private final InternalCurrencyService internalCurrencyService;
    private final UserServiceImpl userServiceImpl;
    private final SettingServiceImpl settingServiceImpl;

    private final ObjectMapper mapper;

    @GetMapping()
    public String settings(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("user", user);
        return "settings/settings";
    }

    @GetMapping("/getSettings")
    @ResponseBody
    public String getSettings() throws JsonProcessingException {
        return mapper.writeValueAsString(settingServiceImpl.getSetting());
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
        settingServiceImpl.updateSetting(setting);
        return mapper.writeValueAsString(null);
    }
}
