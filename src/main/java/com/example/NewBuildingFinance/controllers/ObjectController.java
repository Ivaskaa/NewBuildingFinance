package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.object.ObjectDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.StatusObject;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/objects")
public class ObjectController {
    private final InternalCurrencyServiceImpl currencyService;
    private final ObjectServiceImpl objectServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ObjectMapper mapper;

    @GetMapping()
    public String objects(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "objects";
    }

    @GetMapping("/getObjects")
    @ResponseBody
    public String getObjects(
            Integer page,
            Integer size,
            String field,
            String direction
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(objectServiceImpl.findSortingPage(
                page, size, field, direction));
    }

    @PostMapping("/addObject")
    @ResponseBody
    public String addObject(
            @Valid @RequestBody ObjectDto objectDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (objectServiceImpl.checkPercentages(objectDto.getAgency(), objectDto.getManager())){
            bindingResult.addError(new FieldError("objectDto", "agency", "The sum of percentages must be less than 100"));
            bindingResult.addError(new FieldError("objectDto", "manager", "The sum of percentages must be less than 100"));
        }
        objectServiceImpl.validationCreateWithDatabase(bindingResult, objectDto);
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        objectServiceImpl.save(objectDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateObject")
    @ResponseBody
    public String updateObject(
            @Valid @RequestBody ObjectDto objectDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (objectServiceImpl.checkPercentages(objectDto.getAgency(), objectDto.getManager())){
            bindingResult.addError(new FieldError("objectDto", "agency", "The sum of percentages must be less than 100"));
            bindingResult.addError(new FieldError("objectDto", "manager", "The sum of percentages must be less than 100"));
        }
        objectServiceImpl.validationUpdateWithDatabase(bindingResult, objectDto);
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        objectServiceImpl.update(objectDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getStatusObject")
    @ResponseBody
    public String getStatusObject() throws JsonProcessingException {
        List<Pair<StatusObject, String>> list = new ArrayList<>();
        for(StatusObject statusObject : StatusObject.values()){
            list.add(Pair.of(statusObject, statusObject.getValue()));
        }
        return mapper.writeValueAsString(list);
    }

    @GetMapping("/getObjectById")
    @ResponseBody
    public String getObjectById(
            Long id
    ) throws JsonProcessingException {
        Object object = objectServiceImpl.findById(id);
        return mapper.writeValueAsString(object);
    }

    @PostMapping("/deleteObjectById")
    @ResponseBody
    public String deleteObjectById(
            Long id
    ) throws JsonProcessingException {
        objectServiceImpl.deleteById(id);
        return mapper.writeValueAsString("success");
    }
}
