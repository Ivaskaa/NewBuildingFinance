package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.cashRegister.IncomeSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeUploadDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingUploadDto;
import com.example.NewBuildingFinance.dto.contract.ContractUploadDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
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
import java.text.ParseException;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/cashRegister")
public class CashRegisterController {
    private final InternalCurrencyService internalCurrencyService;
    private final UserService userService;
    private final ObjectService objectService;
    private final FlatService flatService;
    private final FlatPaymentService flatPaymentService;
    private final CashRegisterService cashRegisterService;

    private final ObjectMapper mapper;

    @GetMapping()
    public String cashRegisters(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());
        model.addAttribute("objects", objectService.findAll());

        List<Pair<Economic, String>> economics = new ArrayList<>();
        for(Economic economic : Economic.values()){
            economics.add(Pair.of(economic, economic.getValue()));
        }
        model.addAttribute("economics", economics);

        List<Pair<StatusCashRegister, String>> statuses = new ArrayList<>();
        for(StatusCashRegister status : StatusCashRegister.values()){
            statuses.add(Pair.of(status, status.getValue()));
        }
        model.addAttribute("statuses", statuses);

        List<Pair<Article, String>> articles = new ArrayList<>();
        for(Article article : Article.values()){
            articles.add(Pair.of(article, article.getValue()));
        }
        model.addAttribute("articles", articles);
        model.addAttribute("user", user);
        return "cashRegister/cashRegisters";
    }

    @GetMapping("/getCashRegisters")
    @ResponseBody
    public String getCashRegisters(
            Integer page,
            Integer size,
            String field,
            String direction,

            Long number,
            String dateStart,
            String dateFin,
            String economic,
            String status,
            Long objectId,
            String article,
            Double price,
            Long currencyId,
            String counterparty
    ) throws IOException, ParseException {
        //action
        return mapper.writeValueAsString(cashRegisterService.findSortingAndSpecificationPage(
                page, size, field, direction,
                number,
                dateStart, dateFin,
                economic,
                status,
                objectId,
                article,
                price,
                currencyId,
                counterparty
        ));
    }

    @GetMapping("/income/{id}")
    public String Income(
            @PathVariable(required = false) Long id,
            Long flatPaymentId,
            Model model
    ) {
        if (flatPaymentId != null) {
            CashRegister cashRegister = cashRegisterService.findIncomeByFlatPaymentId(flatPaymentId);
            if (cashRegister != null) {
                return "redirect:/cashRegister/income/" + cashRegister.getId();
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());

        model.addAttribute("cashRegisterId", id);
        model.addAttribute("flatPaymentId", Objects.requireNonNullElse(flatPaymentId, 0));
        model.addAttribute("user", user);
        return "cashRegister/income";
    }

    @GetMapping("/spending/{id}")
    public String Spending(
            @PathVariable(required = false) Long id,
            Long flatId,
            Model model
    ) {
        if (flatId != null) {
            CashRegister cashRegister = cashRegisterService.findSpendingByFlatId(flatId);
            if (cashRegister != null) {
                return "redirect:/cashRegister/spending/" + cashRegister.getId();
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyService.findAll());

        model.addAttribute("cashRegisterId", id);
        model.addAttribute("flatId", Objects.requireNonNullElse(flatId, 0));
        model.addAttribute("user", user);
        return "cashRegister/spending";
    }

    @PostMapping("/addIncome")
    @ResponseBody
    public String addIncome(
            @Valid @RequestBody IncomeSaveDto incomeSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(cashRegisterService.checkNumber(incomeSaveDto.getNumber())){
            bindingResult.addError(new FieldError("incomeSaveDto", "number", "Number is exists"));
        }
        if(incomeSaveDto.getArticle().equals(Article.FLAT_PAYMENT)){
            if (incomeSaveDto.getObjectId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "object", "Must not be empty"));
            }
            if (incomeSaveDto.getFlatId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "flat", "Must not be empty"));
            }
            if (incomeSaveDto.getFlatPaymentId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "flatPayment", "Must not be empty"));
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
        IncomeUploadDto incomeUploadDto = cashRegisterService.saveIncome(incomeSaveDto.build());
        return mapper.writeValueAsString(incomeUploadDto);
    }

    @PostMapping("/updateIncome")
    @ResponseBody
    public String updateIncome(
            @Valid @RequestBody IncomeSaveDto incomeSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        CashRegister cashRegister = cashRegisterService.findById(incomeSaveDto.getId());
        if(!cashRegister.getNumber().equals(incomeSaveDto.getNumber())){
            if(cashRegisterService.checkNumber(incomeSaveDto.getNumber())){
                bindingResult.addError(new FieldError("incomeSaveDto", "number", "Number is exists"));
            }
        }
        if(incomeSaveDto.getArticle().equals(Article.FLAT_PAYMENT)){
            if (incomeSaveDto.getObjectId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "object", "Must not be empty"));
            }
            if (incomeSaveDto.getFlatId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "flat", "Must not be empty"));
            }
            if (incomeSaveDto.getFlatPaymentId() == null){
                bindingResult.addError(new FieldError("incomeSaveDto", "flatPayment", "Must not be empty"));
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
        IncomeUploadDto incomeUploadDto = cashRegisterService.updateIncome(incomeSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/addSpending")
    @ResponseBody
    public String addSpending(
            @Valid @RequestBody SpendingSaveDto spendingSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(cashRegisterService.checkNumber(spendingSaveDto.getNumber())){
            bindingResult.addError(new FieldError("spendingSaveDto", "number", "Number is exists"));
        }
        if(spendingSaveDto.getArticle().equals(Article.COMMISSION_MANAGER)){
            if (spendingSaveDto.getObjectId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
            }
            if (spendingSaveDto.getFlatId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
            }
            if (spendingSaveDto.getManagerId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
            }
        } else if(spendingSaveDto.getArticle().equals(Article.COMMISSION_AGENCIES)){
            if (spendingSaveDto.getObjectId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
            }
            if (spendingSaveDto.getFlatId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
            }
            if (spendingSaveDto.getRealtorId() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
            }
        } else {
            if (spendingSaveDto.getCounterparty() == null){
                bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
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
        SpendingUploadDto spendingUploadDto = cashRegisterService.saveSpending(spendingSaveDto.build());
        return mapper.writeValueAsString(spendingUploadDto);
    }

    @PostMapping("/deleteIncomeById")
    @ResponseBody
    public String deleteIncomeById(
            Long id
    ) throws JsonProcessingException {
        cashRegisterService.deleteIncomeById(id);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getIncomeById")
    @ResponseBody
    public String getIncomeById(
            Long id
    ) throws JsonProcessingException {
        IncomeUploadDto cashRegister = cashRegisterService.findIncomeById(id);
        return mapper.writeValueAsString(cashRegister);
    }

    @GetMapping("/getSpendingById")
    @ResponseBody
    public String getSpendingById(
            Long id
    ) throws JsonProcessingException {
        SpendingUploadDto cashRegister = cashRegisterService.findSpendingById(id);
        return mapper.writeValueAsString(cashRegister);
    }

    // others

    @GetMapping("/getCurrencies")
    @ResponseBody
    public String getCurrencies() throws JsonProcessingException {
        return mapper.writeValueAsString(internalCurrencyService.findAll());
    }

    @GetMapping("/getArticlesForIncome")
    @ResponseBody
    public String getArticles() throws JsonProcessingException {
        List<Pair<Article, String>> articles = new ArrayList<>();
        articles.add(Pair.of(Article.FLAT_PAYMENT, Article.FLAT_PAYMENT.getValue()));
        articles.add(Pair.of(Article.OTHER_PAYMENT, Article.OTHER_PAYMENT.getValue()));
        return mapper.writeValueAsString(articles);
    }

    @GetMapping("/getObjects")
    @ResponseBody
    public String getObjects() throws JsonProcessingException {
        return mapper.writeValueAsString(objectService.findAllOnSale());
    }

    @GetMapping("/getFlatsWithContractWithFlatPaymentsByObjectId")
    @ResponseBody
    public String getFlatsWithContractWithFlatPaymentsByObjectId(
            Long id,
            Long flatId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatService.getWithContractWithFlatPaymentByObjectId(id, flatId));
    }

    @GetMapping("/getFlatPaymentsByFlatId")
    @ResponseBody
    public String getFlatPaymentsByFlatId(
            Long id,
            Long flatPaymentId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatPaymentService.getByFlatId(id, flatPaymentId));
    }

    @GetMapping("/getFlatPaymentById")
    @ResponseBody
    public String getFlatPaymentById(
            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatPaymentService.findById(id));
    }

    @GetMapping("/getCurrencyById")
    @ResponseBody
    public String getCurrencyById(
            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(internalCurrencyService.findById(id));
    }

}
