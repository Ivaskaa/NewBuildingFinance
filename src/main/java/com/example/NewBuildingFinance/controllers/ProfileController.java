package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.auth.ProfileDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.auth.ProfileService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class ProfileController {
    private final InternalCurrencyService currencyService;
    private final ProfileService profileService;
    private final ObjectMapper mapper;

    @GetMapping( "/profile" )
    public String profile(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = profileService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping(value = "/updateProfile", consumes = {"multipart/form-data"})
    @ResponseBody
    public String updateProfile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("profile") @Valid ProfileDto profile,
            BindingResult bindingResult
    ) throws IOException, ParseException {
        //validation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = profileService.loadUserByUsername(authentication.getName());
        if (profileService.checkPhone(profile.getPhone())) {
            bindingResult.addError(new FieldError("profileDto", "phone", "Phone must be valid"));
        }
        if (profileService.checkViber(profile.getViber())) {
            bindingResult.addError(new FieldError("profileDto", "viber", "Viber must be valid"));
        }
        if (!user.getUsername().equals(profile.getUsername())) {
            if (profileService.checkEmail(profile.build())) {
                bindingResult.addError(new FieldError("profileDto", "username", "Email is already registered"));
            }
        }
        if (profileService.checkPassword(user, profile.getPassword())) {
            bindingResult.addError(new FieldError("profileDto", "password", "Wrong password"));
        } else {
            if (profileService.checkEqualsPassword(profile.getFirstPassword(), profile.getSecondPassword())) {
                bindingResult.addError(new FieldError("profileDto", "firstPassword", "Passwords must be equals"));
                bindingResult.addError(new FieldError("profileDto", "secondPassword", "Passwords must be equals"));
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
        profileService.update(profile.build(), file);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("getProfile")
    @ResponseBody
    public String getProfile() throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = profileService.loadUserByUsername(authentication.getName());
        return mapper.writeValueAsString(user);
    }
}