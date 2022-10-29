package com.example.NewBuildingFinance.controllers.settings;

import com.example.NewBuildingFinance.dto.auth.UserDto;
import com.example.NewBuildingFinance.entities.auth.User;
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
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class UserController {
    private final InternalCurrencyService currencyService;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final ObjectMapper mapper;

    @GetMapping( "/users" )
    public String users(
            Model model
    ) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping("/addUser")
    @ResponseBody
    public String addUser(
            @Valid @RequestBody UserDto userDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (userService.checkPhone(userDto.build())) {
            bindingResult.addError(new FieldError("userDto", "phone", "Phone must be valid"));
        }
        if (userService.checkEmail(userDto.build())) {
            bindingResult.addError(new FieldError("userDto", "username", "Email is already registered"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        userService.save(userDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public String updateUser(
            @Valid @RequestBody UserDto userDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (!userDto.getLatestUsername().equals(userDto.getUsername())) {
            if (userService.checkEmail(userDto.build())) {
                bindingResult.addError(new FieldError("userDto", "username", "Email is already registered"));
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
        userService.update(userDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/changeUserActiveById")
    @ResponseBody
    public String changeUserActiveById(
            Long id
    ) throws JsonProcessingException {
        System.out.println(id);
        userService.changeUserActiveById(id);
        return mapper.writeValueAsString(null);
    }
    @GetMapping("/getUsers")
    @ResponseBody
    public String getUsers(
            Integer page,
            Integer size,
            String field,
            String direction
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(userService.findSortingPage(
                page, size, field, direction));
    }

    @GetMapping("/getUserById")
    @ResponseBody
    public String getUserById(
            Long id
    ) throws JsonProcessingException {
        User user = userService.findById(id);
        return mapper.writeValueAsString(user);
    }

    @PostMapping("/deleteUserById")
    @ResponseBody
    public String deleteUserById(
            Long id
    ) throws JsonProcessingException {
        userService.deleteById(id);
        return mapper.writeValueAsString("success");
    }

    @PostMapping("/resendInvitation")
    @ResponseBody
    public String resendInvitation(
            Long id
    ) throws JsonProcessingException {
        User user = userService.findById(id);
        userService.sendRegistrationEmail(user);
        return mapper.writeValueAsString("success");
    }
}
