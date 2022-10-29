package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentDto;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.service.*;
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
@RequestMapping("/flats")
public class FlatPaymentController {
    private final FlatPaymentService flatPaymentService;
    private final ObjectMapper mapper;

    @GetMapping("/getPaymentsByFlatId")
    @ResponseBody
    public String getFlats(
            Integer page,
            Integer size,
            String field,
            String direction,

            Long flatId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatPaymentService.findPage(
                page, size, field, direction, flatId));
    }

    @PostMapping("/addPayment")
    @ResponseBody
    public String addPayment(
            @Valid @RequestBody FlatPaymentDto flatPaymentDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (flatPaymentService.checkNumber(flatPaymentDto.getNumber(), flatPaymentDto.getFlatId())) {
            bindingResult.addError(new FieldError("flatPaymentDto", "number", "This number already exists"));
        }
        if(flatPaymentService.checkPlaned(flatPaymentDto.getPlanned(), flatPaymentDto.getFlatId())){
            bindingResult.addError(new FieldError("flatPaymentDto", "planned", "The sum of planned payment can`t exceed sale price"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }


        //action
        flatPaymentService.save(flatPaymentDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updatePayment")
    @ResponseBody
    public String updatePayment(
            @Valid @RequestBody FlatPaymentDto flatPaymentDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        FlatPayment flatPayment = flatPaymentService.findById(flatPaymentDto.getId());
        if(!flatPayment.getNumber().equals(flatPaymentDto.getNumber())) {
            if (flatPaymentService.checkNumber(flatPaymentDto.getNumber(), flatPaymentDto.getFlatId())) {
                bindingResult.addError(new FieldError("flatPaymentDto", "number", "This number already exists"));
            }
        }
        if(flatPaymentService.checkPlanedEdit(flatPaymentDto.getId(), flatPaymentDto.getPlanned(), flatPaymentDto.getFlatId())){
            bindingResult.addError(new FieldError("flatPaymentDto", "planned", "The sum of planned payment can`t exceed sale price"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        flatPaymentService.update(flatPaymentDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getPaymentById")
    @ResponseBody
    public String getPaymentById(
            Long id
    ) throws JsonProcessingException {
        FlatPayment flatPayment = flatPaymentService.findById(id);
        return mapper.writeValueAsString(flatPayment);
    }

    @PostMapping("/deletePaymentById")
    @ResponseBody
    public String deletePaymentById(
            Long id
    ) throws JsonProcessingException {
        flatPaymentService.deleteById(id);
        return mapper.writeValueAsString("success");
    }
}
