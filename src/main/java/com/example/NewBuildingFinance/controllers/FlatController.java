package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.BuyerDto;
import com.example.NewBuildingFinance.dto.flat.FlatSaveDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.service.*;
import com.example.NewBuildingFinance.service.auth.UserService;
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
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/flats")
public class FlatController {
    private final InternalCurrencyService currencyService;
    private final UserService userService;
    private final FlatService flatService;
    private final AgencyService agencyService;
    private final RealtorService realtorService;
    private final BuyerService buyerService;
    private final ObjectService objectService;
    private final ObjectMapper mapper;

    @GetMapping()
    public String flats(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectService.findAll());
        List<Pair<StatusFlat, String>> list = new ArrayList<>();
        for(StatusFlat statusObject : StatusFlat.values()){
            list.add(Pair.of(statusObject, statusObject.getValue()));
        }
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("statuses", list);
        model.addAttribute("user", user);
        return "flat/flats";
    }

    @GetMapping("/flat/{id}")
    public String flat(
            @PathVariable(required = false) Long id,
            Model model
    ) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("flatId", id);
        model.addAttribute("user", user);
        return "flat/flat";
    }

    @GetMapping("/getFlats")
    @ResponseBody
    public String getFlats(
            Integer page,
            Integer size,
            String field,
            String direction,

            Optional<Integer> number,
            Optional<Long> objectId,
            Optional<String> status,
            Optional<Integer> areaStart,
            Optional<Integer> areaFin,
            Optional<Integer> priceStart,
            Optional<Integer> priceFin,
            Optional<Integer> advanceStart,
            Optional<Integer> advanceFin
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatService.findSortingAndSpecificationPage(
                page, size, field, direction,
                number,
                objectId,
                status,
                areaStart, areaFin,
                priceStart, priceFin,
                advanceStart, advanceFin));
    }

    @PostMapping("/addFlat")
    @ResponseBody
    public String addFlat(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        System.out.println(flatSaveDto);
        if(flatService.checkFlatNumber(flatSaveDto.getNumber(), flatSaveDto.getObjectId())){
            bindingResult.addError(new FieldError("flatSaveDto", "number", "In selected object the flat with this number is exists"));
        }
        if(flatService.checkPrice(flatSaveDto.getPrice(), flatSaveDto.getSalePrice())){
            bindingResult.addError(new FieldError("flatSaveDto", "price", "Price must be rather then sale price"));
            bindingResult.addError(new FieldError("flatSaveDto", "salePrice", "Price must be rather then sale price"));
        }
        if(flatService.checkPercentages(flatSaveDto.getAgency(), flatSaveDto.getManager())){
            bindingResult.addError(new FieldError("flatSaveDto", "agency", "The sum of percentages must be less than 100"));
            bindingResult.addError(new FieldError("flatSaveDto", "manager", "The sum of percentages must be less than 100"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }
        //action
        Flat flat = flatService.save(flatSaveDto.build());
        return mapper.writeValueAsString(flat.getId());
    }

    @PostMapping("/updateFlat")
    @ResponseBody
    public String updateFlat(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        Flat flat = flatService.findById(flatSaveDto.getId());
        if(!flat.getNumber().equals(flatSaveDto.getNumber())){
            if(flatService.checkFlatNumber(flatSaveDto.getNumber(), flatSaveDto.getObjectId())){
                bindingResult.addError(new FieldError("flatSaveDto", "number", "In selected object the flat with this number is exists"));
            }
        }
        if(flatService.checkPrice(flatSaveDto.getPrice(), flatSaveDto.getSalePrice())){
            bindingResult.addError(new FieldError("flatSaveDto", "price", "Price must be rather then sale price"));
            bindingResult.addError(new FieldError("flatSaveDto", "salePrice", "Price must be rather then sale price"));
        }
        if(flatService.checkPercentages(flatSaveDto.getAgency(), flatSaveDto.getManager())){
            bindingResult.addError(new FieldError("flatSaveDto", "agency", "The sum of percentages must be less than 100"));
            bindingResult.addError(new FieldError("flatSaveDto", "manager", "The sum of percentages must be less than 100"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        flatService.update(flatSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateFlatWithBuyer")
    @ResponseBody
    public String updateFlatWithBuyer(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        Flat flat = flatService.findById(flatSaveDto.getId());
        if(!flat.getNumber().equals(flatSaveDto.getNumber())){
            if(flatService.checkFlatNumber(flatSaveDto.getNumber(), flatSaveDto.getObjectId())){
                bindingResult.addError(new FieldError("flatSaveDto", "number", "In selected object the flat with this number is exists"));
            }
        }
        if(flatService.checkPrice(flatSaveDto.getPrice(), flatSaveDto.getSalePrice())){
            bindingResult.addError(new FieldError("flatSaveDto", "price", "Price must be rather then sale price"));
            bindingResult.addError(new FieldError("flatSaveDto", "salePrice", "Price must be rather then sale price"));
        }
        if(flatService.checkStatus(flatSaveDto.getStatus())){
            bindingResult.addError(new FieldError("flatSaveDto", "status", "Flat status must be active"));
        }
        if(flatService.checkPercentages(flatSaveDto.getAgency(), flatSaveDto.getManager())){
            bindingResult.addError(new FieldError("flatSaveDto", "agency", "The sum of percentages must be less than 100"));
            bindingResult.addError(new FieldError("flatSaveDto", "manager", "The sum of percentages must be less than 100"));
        }
        if(flatSaveDto.getBuyerId() == null){
            bindingResult.addError(new FieldError("flatSaveDto", "buyer", "Must not be empty"));
        } else if(flatSaveDto.getRealtorId() == null){
            bindingResult.addError(new FieldError("flatSaveDto", "realtor", "Must not be empty"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        flatService.update(flatSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getFlatById")
    @ResponseBody
    public String getFlatById(
            Long id
    ) throws JsonProcessingException {
        Flat flat = flatService.findById(id);
        return mapper.writeValueAsString(flat);
    }

    @PostMapping("/deleteFlatById")
    @ResponseBody
    public String deleteFlatById(
            Long id
    ) throws JsonProcessingException {
        flatService.deleteById(id);
        return mapper.writeValueAsString("success");
    }

    // another

    @GetMapping("/getAllAgencies")
    @ResponseBody
    public String getAllAgencies() throws JsonProcessingException {
        List<Agency> agencyList = agencyService.findAll();
        return mapper.writeValueAsString(agencyList);
    }

    @GetMapping("/getRealtorsByAgenciesId")
    @ResponseBody
    public String getRealtorsByAgenciesId(
            Long id
    ) throws JsonProcessingException {
        List<Realtor> realtorList = realtorService.findAllByAgencyId(id);
        return mapper.writeValueAsString(realtorList);
    }

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerService.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @GetMapping("/getRealtorById")
    @ResponseBody
    public String getRealtorById(
            Long id
    ) throws JsonProcessingException {
        Realtor realtor = realtorService.findById(id);
        return mapper.writeValueAsString(realtor);
    }

    @GetMapping("/getBuyersByName")
    @ResponseBody
    public String getBuyersByName(
            String q
    ) throws JsonProcessingException {
        List<Buyer> buyers = buyerService.findByName(q);
        return mapper.writeValueAsString(buyers);
    }

    @GetMapping("/getStatuses")
    @ResponseBody
    public String getStatuses() throws JsonProcessingException {
        List<Pair<StatusFlat, String>> list = new ArrayList<>();
        for(StatusFlat statusObject : StatusFlat.values()){
            list.add(Pair.of(statusObject, statusObject.getValue()));
        }
        return mapper.writeValueAsString(list);
    }

    @GetMapping("/getAllOnSaleObjects")
    @ResponseBody
    public String getAllOnSaleObjects() throws JsonProcessingException {
        return mapper.writeValueAsString(objectService.findAllOnSale());
    }

    // for buyer

    @PostMapping("/addBuyer")
    @ResponseBody
    public String addBuyer(
            @Valid @RequestBody BuyerDto buyerDto,
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
        Buyer buyer = buyerService.save(buyerDto.build());
        return mapper.writeValueAsString(buyer.getId());
    }

    @GetMapping("/getAllManagers")
    @ResponseBody
    public String getAllManagers() throws JsonProcessingException {
        return mapper.writeValueAsString(userService.findManagers());
    }
}
