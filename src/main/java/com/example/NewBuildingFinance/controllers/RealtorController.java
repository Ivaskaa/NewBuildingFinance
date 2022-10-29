package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.RealtorDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.service.InternalCurrencyService;
import com.example.NewBuildingFinance.service.RealtorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/agencies/realtors")
public class RealtorController {
    private final RealtorService realtorService;
    private final ObjectMapper mapper;

    @GetMapping("/getRealtorsByAgencyId")
    @ResponseBody
    public String getRealtorsByAgencyId(
            Integer page,
            Integer size,
            String field,
            String direction,

            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(realtorService.findPageByAgencyId(
                page, size, field, direction, id));
    }

    @PostMapping("/addRealtor")
    @ResponseBody
    public String addRealtor(
            @Valid @RequestBody RealtorDto realtorDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (realtorService.checkPhone(realtorDto.getPhone())) {
            bindingResult.addError(new FieldError("userDto", "phone", "Phone must be valid"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        realtorService.save(realtorDto.build(), realtorDto.getAgencyId());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateRealtor")
    @ResponseBody
    public String updateRealtor(
            @Valid @RequestBody RealtorDto realtorDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (realtorService.checkPhone(realtorDto.getPhone())) {
            bindingResult.addError(new FieldError("userDto", "phone", "Phone must be valid"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            System.out.println(errors);
            return mapper.writeValueAsString(errors);
        }
        System.out.println(realtorDto);
        //action
        realtorService.update(realtorDto.build(), realtorDto.getAgencyId());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getRealtorById")
    @ResponseBody
    public String getRealtorById(
            Long id
    ) throws JsonProcessingException {
        Realtor object = realtorService.findById(id);
        return mapper.writeValueAsString(object);
    }

    @PostMapping("/deleteRealtorById")
    @ResponseBody
    public String deleteRealtorById(
            Long id
    ) throws JsonProcessingException {
        realtorService.deleteById(id);
        return mapper.writeValueAsString("success");
    }
}
