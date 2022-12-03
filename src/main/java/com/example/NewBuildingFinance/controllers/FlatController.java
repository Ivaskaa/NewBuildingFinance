package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.buyer.BuyerSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDtoForFlat;
import com.example.NewBuildingFinance.dto.flat.FlatSaveDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import com.example.NewBuildingFinance.service.cashRegister.CashRegisterServiceImpl;
import com.example.NewBuildingFinance.service.flat.FlatServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.example.NewBuildingFinance.service.realtor.RealtorServiceImpl;
import com.example.NewBuildingFinance.service.statistic.StatisticServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/flats")
public class FlatController {
    private final InternalCurrencyServiceImpl currencyService;
    private final StatisticServiceImpl statisticService;
    private final UserServiceImpl userServiceImpl;

    private final FlatServiceImpl flatServiceImpl;
    private final AgencyServiceImpl agencyServiceImpl;
    private final RealtorServiceImpl realtorServiceImpl;
    private final BuyerServiceImpl buyerServiceImpl;
    private final ObjectServiceImpl objectServiceImpl;
    private final CashRegisterServiceImpl cashRegisterService;

    private final ObjectMapper mapper;

    @GetMapping()
    public String flats(
            Model model
    ) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectServiceImpl.findAll());
        List<Pair<StatusFlat, String>> list = new ArrayList<>();
        for(StatusFlat statusObject : StatusFlat.values()){
            list.add(Pair.of(statusObject, statusObject.getValue()));
        }
        model.addAttribute("statuses", list);
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("statistic", statisticService.getMainMonthlyStatistic(null, "", ""));
        model.addAttribute("user", user);
        return "flat/flats";
    }

    @GetMapping("/flat/{id}")
    public String flat(
            @PathVariable(required = false) Long id,
            Long flatPaymentId,
            Model model
    ) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("flatId", id);
        model.addAttribute("userId", user.getId());
        model.addAttribute("flatPaymentId", Objects.requireNonNullElse(flatPaymentId, 0));
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

            Integer number,
            Long objectId,
            String status,
            Double areaStart,
            Double areaFin,
            Integer priceStart,
            Integer priceFin,
            Integer advanceStart,
            Integer advanceFin,
            Integer enteredStart,
            Integer enteredFin,
            Integer remainsStart,
            Integer remainsFin
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatServiceImpl.findSortingAndSpecificationPage(
                page, size, field, direction,
                number,
                objectId,
                status,
                areaStart, areaFin,
                priceStart, priceFin,
                advanceStart, advanceFin,
                enteredStart, enteredFin,
                remainsStart, remainsFin
        ));
    }

    @PostMapping("/addFlat")
    @ResponseBody
    public String addFlat(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(flatServiceImpl.validationWithoutDatabase(bindingResult, flatSaveDto) && !bindingResult.hasErrors()){
            flatServiceImpl.validationCreateWithDatabase(bindingResult, flatSaveDto);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return mapper.writeValueAsString(errors);
        }

        //action
        Flat flat = flatServiceImpl.save(flatSaveDto.build());
        return mapper.writeValueAsString(flat.getId());
    }

    @PostMapping("/updateFlat")
    @ResponseBody
    public String updateFlat(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation

        if(flatServiceImpl.validationWithoutDatabase(bindingResult, flatSaveDto) && !bindingResult.hasErrors()){
            flatServiceImpl.validationUpdateWithDatabase(bindingResult, flatSaveDto);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return mapper.writeValueAsString(errors);
        }

        //action
        flatServiceImpl.update(flatSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/updateFlatWithBuyer")
    @ResponseBody
    public String updateFlatWithBuyer(
            @Valid @RequestBody FlatSaveDto flatSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(flatServiceImpl.validationCheckStatus(flatSaveDto.getStatus())){
            bindingResult.addError(new FieldError("flatSaveDto", "status", "Flat status must be active"));
        }
        if(flatSaveDto.getBuyerId() == null){
            bindingResult.addError(new FieldError("flatSaveDto", "buyer", "Must not be empty"));
        } else if(flatSaveDto.getRealtorId() == null){
            bindingResult.addError(new FieldError("flatSaveDto", "realtor", "Must not be empty"));
        }

        if(flatServiceImpl.validationWithoutDatabase(bindingResult, flatSaveDto) && !bindingResult.hasErrors()){
            flatServiceImpl.validationUpdateWithDatabase(bindingResult, flatSaveDto);
        }

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return mapper.writeValueAsString(errors);
        }

        //action
        flatServiceImpl.update(flatSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getFlatById")
    @ResponseBody
    public String getFlatById(
            Long id
    ) throws JsonProcessingException {
        Flat flat = flatServiceImpl.findById(id);
        return mapper.writeValueAsString(flat);
    }

    @PostMapping("/deleteFlatById")
    @ResponseBody
    public String deleteFlatById(
            Long id
    ) throws JsonProcessingException {
        flatServiceImpl.deleteById(id);
        return mapper.writeValueAsString("success");
    }

    @GetMapping("/getXlsx")
    public ResponseEntity<byte[]> getXlsx() throws IOException {
        return flatServiceImpl.getXlsx();
    }

    @GetMapping("/getXlsxExample")
    public ResponseEntity<byte[]> getXlsxExample() throws IOException {
        return flatServiceImpl.getXlsxExample();
    }

    @GetMapping("/getXlsxErrors")
    public ResponseEntity<byte[]> getXlsxErrors() throws IOException {
        return flatServiceImpl.getXlsxErrors();
    }

    @PostMapping("/setXlsx")
    @ResponseBody
    public String setXlsx(
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        Map<String, String> errors = new HashMap<>();
        if(file == null){
            errors.put("file", "Must not be empty");
            return mapper.writeValueAsString(errors);
        }
        if(!flatServiceImpl.setXlsx(file)){
            errors.put("fileErrors", "Check errors in xlsx file");
            return mapper.writeValueAsString(errors);
        } else {
            return mapper.writeValueAsString(null);
        }
    }

    // another

    @GetMapping("/getAllAgenciesByDeletedFalse")
    @ResponseBody
    public String getAllAgenciesByDeletedFalse(
            Long agencyId
    ) throws JsonProcessingException {
        System.out.println(agencyId);
        List<Agency> agencyList = agencyServiceImpl.findAllByDeletedFalse(agencyId);
        return mapper.writeValueAsString(agencyList);
    }

    @GetMapping("/getRealtorsByAgenciesId")
    @ResponseBody
    public String getRealtorsByAgenciesId(
            Long agencyId,
            Long realtorId
    ) throws JsonProcessingException {
        List<Realtor> realtorList = realtorServiceImpl.findAllByAgencyIdOrRealtorId(agencyId, realtorId);
        return mapper.writeValueAsString(realtorList);
    }

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerServiceImpl.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @GetMapping("/getRealtorById")
    @ResponseBody
    public String getRealtorById(
            Long id
    ) throws JsonProcessingException {
        Realtor realtor = realtorServiceImpl.findById(id);
        return mapper.writeValueAsString(realtor);
    }

    @GetMapping("/getBuyersByName")
    @ResponseBody
    public String getBuyersByName(
            String q
    ) throws JsonProcessingException {
        List<Buyer> buyers = buyerServiceImpl.findByName(q);
        return mapper.writeValueAsString(buyers);
    }

    @GetMapping("/getStatuses")
    @ResponseBody
    public String getStatuses(Long flatId) throws JsonProcessingException {
        List<Pair<StatusFlat, String>> list = flatServiceImpl.getStatusesByFlatId(flatId);
        return mapper.writeValueAsString(list);
    }

    @GetMapping("/getAllOnSaleObjects")
    @ResponseBody
    public String getAllOnSaleObjects(
            Long objectId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(objectServiceImpl.findAllOnSaleOrObjectId(objectId));
    }

    // for buyer

    @PostMapping("/addBuyer")
    @ResponseBody
    public String addBuyer(
            @Valid @RequestBody BuyerSaveDto buyerSaveDto,
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
        Buyer buyer = buyerServiceImpl.save(buyerSaveDto.build());
        return mapper.writeValueAsString(buyer.getId());
    }

    @GetMapping ("/getUserPermissionsById")
    @ResponseBody
    public String getUserPermissionsById(
            Long id
    ) throws JsonProcessingException {
        List<String> permissions = userServiceImpl.getUserPermissionsById(id);
        return mapper.writeValueAsString(permissions);
    }

    @GetMapping("/getAllManagers")
    @ResponseBody
    public String getAllManagers(
            Long managerId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(userServiceImpl.findManagers(managerId));
    }


    @GetMapping ("/getCashRegisterByFlatId")
    @ResponseBody
    public String getCashRegisterByFlatId(
            Long id
    ) throws JsonProcessingException {
        List<CashRegisterTableDtoForFlat> cashRegisters = cashRegisterService.getCashRegistersByFlatId(id);
        return mapper.writeValueAsString(cashRegisters);
    }
}
