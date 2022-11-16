package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@AllArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping()
    public String registerUser(
            @RequestParam(required = false) String token,
            final Model model
    ) throws ParseException {
        if(token == null || token.isEmpty()){
            model.addAttribute("tokenFailedMessage", "Your token validation failed");
            return "auth/registration";
        }
        User user = userServiceImpl.findUserByToken(token);
        if(user == null){
            model.addAttribute("tokenFailedMessage", "Your token validation failed");
            return "auth/registration";
        }
        if(user.getPassword()!= null){
            return "redirect:/login";
        }

        model.addAttribute("tokenMessage", "Your token verification is successful");
        model.addAttribute("token", token);
        return "auth/registration";
    }

    @PostMapping("/password")
    public String registrationPassword(
            String token,
            String password,
            String repeatPassword,
            Model model
    ) throws ParseException {
        System.out.println(token);
        System.out.println(password);
        System.out.println(repeatPassword);
        if(token == null || token.isEmpty()){
            model.addAttribute("tokenFailedMessage", "Your token validation failed");
            return "auth/registration";
        }
        User user = userServiceImpl.findUserByToken(token);
        if(user == null){
            model.addAttribute("tokenFailedMessage", "Your token validation failed");
            return "auth/registration";
        }
        if(!password.equals(repeatPassword)){
            model.addAttribute("passwordMessage", "Passwords must be equals");
            return "auth/registration";
        }
        userServiceImpl.savePassword(user, password);
        userServiceImpl.deleteToken(token);
        return "redirect:/login";
    }
}
