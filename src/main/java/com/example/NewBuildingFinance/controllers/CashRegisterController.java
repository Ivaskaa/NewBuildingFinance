package com.example.NewBuildingFinance.controllers;

import com.example.NewBuildingFinance.dto.RealtorDto;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeUploadDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingUploadDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.cashRegister.CashRegisterServiceImpl;
import com.example.NewBuildingFinance.service.flat.FlatServiceImpl;
import com.example.NewBuildingFinance.service.flatPayment.FlatPaymentServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.example.NewBuildingFinance.service.realtor.RealtorServiceImpl;
import com.example.NewBuildingFinance.service.statistic.StatisticServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/cashRegister")
public class CashRegisterController {
    private final InternalCurrencyServiceImpl internalCurrencyServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final StatisticServiceImpl statisticService;
    private final ObjectServiceImpl objectServiceImpl;
    private final FlatServiceImpl flatServiceImpl;
    private final FlatPaymentServiceImpl flatPaymentServiceImpl;
    private final AgencyServiceImpl agencyServiceImpl;
    private final RealtorServiceImpl realtorServiceImpl;
    private final CashRegisterServiceImpl cashRegisterServiceImpl;

    private final ObjectMapper mapper;

    @GetMapping()
    public String cashRegisters(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());
        model.addAttribute("objects", objectServiceImpl.findAll());

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
        model.addAttribute("statistic", statisticService.getCashRegisterBoxes());
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
            Double priceStart,
            Double priceFin,
            Long currencyId,
            String counterparty
    ) throws IOException, ParseException {
        //action
        return mapper.writeValueAsString(cashRegisterServiceImpl.findSortingAndSpecificationPage(
                page, size, field, direction,
                number,
                dateStart, dateFin,
                economic,
                status,
                objectId,
                article,
                priceStart, priceFin,
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
            CashRegister cashRegister = cashRegisterServiceImpl.findIncomeByFlatPaymentId(flatPaymentId);
            if (cashRegister != null) {
                return "redirect:/cashRegister/income/" + cashRegister.getId();
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());

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
        if(cashRegisterServiceImpl.checkNumber(incomeSaveDto.getNumber())){
            bindingResult.addError(new FieldError("incomeSaveDto", "number", "Number is exists"));
        }
        if (incomeSaveDto.getArticle() != null) {
            if (incomeSaveDto.getArticle().equals(Article.FLAT_PAYMENT)) {
                if (incomeSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "object", "Must not be empty"));
                }
                if (incomeSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "flat", "Must not be empty"));
                }
                if (incomeSaveDto.getFlatPaymentId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "flatPayment", "Must not be empty"));
                }
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
        CashRegister cashRegister = cashRegisterServiceImpl.saveIncome(incomeSaveDto.build());
        return mapper.writeValueAsString(cashRegister.getId());
    }

    @PostMapping("/updateIncome")
    @ResponseBody
    public String updateIncome(
            @Valid @RequestBody IncomeSaveDto incomeSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        CashRegister cashRegister = cashRegisterServiceImpl.findById(incomeSaveDto.getId());
        if(!cashRegister.getNumber().equals(incomeSaveDto.getNumber())){
            if(cashRegisterServiceImpl.checkNumber(incomeSaveDto.getNumber())){
                bindingResult.addError(new FieldError("incomeSaveDto", "number", "Number is exists"));
            }
        }
        if (incomeSaveDto.getArticle() != null) {
            if (incomeSaveDto.getArticle().equals(Article.FLAT_PAYMENT)) {
                if (incomeSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "object", "Must not be empty"));
                }
                if (incomeSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "flat", "Must not be empty"));
                }
                if (incomeSaveDto.getFlatPaymentId() == null) {
                    bindingResult.addError(new FieldError("incomeSaveDto", "flatPayment", "Must not be empty"));
                }
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
        IncomeUploadDto incomeUploadDto = cashRegisterServiceImpl.updateIncome(incomeSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @PostMapping("/addSpending")
    @ResponseBody
    public String addSpending(
            @Valid @RequestBody SpendingSaveDto spendingSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if(cashRegisterServiceImpl.checkNumber(spendingSaveDto.getNumber())){
            bindingResult.addError(new FieldError("spendingSaveDto", "number", "Number is exists"));
        }
        if (spendingSaveDto.getArticle() != null) {
            if (spendingSaveDto.getArticle().equals(Article.COMMISSION_MANAGER)) {
                if (spendingSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
                }
                if (spendingSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
                }
                if (spendingSaveDto.getManagerId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "manager", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.COMMISSION_AGENCIES)) {
                if (spendingSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
                }
                if (spendingSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
                }
                if (spendingSaveDto.getRealtorId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "realtor", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.CONSTRUCTION_COSTS)) {
                if (spendingSaveDto.getCounterpartyName() == null || spendingSaveDto.getCounterpartyName().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
                }
                if (spendingSaveDto.getCounterpartySurname() == null || spendingSaveDto.getCounterpartySurname().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.MONEY_FOR_DIRECTOR)) {
                if (spendingSaveDto.getCounterpartyName() == null || spendingSaveDto.getCounterpartyName().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "director", "Must not be empty"));
                }
                if (spendingSaveDto.getCounterpartySurname() == null || spendingSaveDto.getCounterpartySurname().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "director", "Must not be empty"));
                }
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
        CashRegister cashRegister = cashRegisterServiceImpl.saveSpending(spendingSaveDto.build());
        return mapper.writeValueAsString(cashRegister.getId());
    }

    @PostMapping("/updateSpending")
    @ResponseBody
    public String updateSpending(
            @Valid @RequestBody SpendingSaveDto spendingSaveDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        CashRegister cashRegister = cashRegisterServiceImpl.findById(spendingSaveDto.getId());
        if(!cashRegister.getNumber().equals(spendingSaveDto.getNumber())){
            if(cashRegisterServiceImpl.checkNumber(spendingSaveDto.getNumber())){
                bindingResult.addError(new FieldError("spendingSaveDto", "number", "Number is exists"));
            }
        }
        if (spendingSaveDto.getArticle() != null) {
            if (spendingSaveDto.getArticle().equals(Article.COMMISSION_MANAGER)) {
                if (spendingSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
                }
                if (spendingSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
                }
                if (spendingSaveDto.getManagerId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "manager", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.COMMISSION_AGENCIES)) {
                if (spendingSaveDto.getObjectId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "object", "Must not be empty"));
                }
                if (spendingSaveDto.getFlatId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "flat", "Must not be empty"));
                }
                if (spendingSaveDto.getRealtorId() == null) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "realtor", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.CONSTRUCTION_COSTS)) {
                if (spendingSaveDto.getCounterpartyName() == null || spendingSaveDto.getCounterpartyName().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
                }
                if (spendingSaveDto.getCounterpartySurname() == null || spendingSaveDto.getCounterpartySurname().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "counterparty", "Must not be empty"));
                }
            } else if (spendingSaveDto.getArticle().equals(Article.MONEY_FOR_DIRECTOR)) {
                if (spendingSaveDto.getCounterpartyName() == null || spendingSaveDto.getCounterpartyName().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "director", "Must not be empty"));
                }
                if (spendingSaveDto.getCounterpartySurname() == null || spendingSaveDto.getCounterpartySurname().equals("")) {
                    bindingResult.addError(new FieldError("spendingSaveDto", "director", "Must not be empty"));
                }
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
        cashRegisterServiceImpl.updateSpending(spendingSaveDto.build());
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getIncomeById")
    @ResponseBody
    public String getIncomeById(
            Long id
    ) throws JsonProcessingException {
        IncomeUploadDto cashRegister = cashRegisterServiceImpl.findIncomeById(id);
        return mapper.writeValueAsString(cashRegister);
    }

    @GetMapping("/getSpendingById")
    @ResponseBody
    public String getSpendingById(
            Long id
    ) throws JsonProcessingException {
        SpendingUploadDto cashRegister = cashRegisterServiceImpl.findSpendingById(id);
        return mapper.writeValueAsString(cashRegister);
    }

    @GetMapping("/getPdfIncome/{id}")
    public ResponseEntity<byte[]> getPdfIncome(
            @PathVariable(required = false) Long id
    ) throws IOException, DocumentException, TransformerException {
        //validation
        if(cashRegisterServiceImpl.checkCashRegister(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //action
        return cashRegisterServiceImpl.getPdfIncome(id);
    }

    @GetMapping("/getPdfSpending/{id}")
    public ResponseEntity<byte[]> getPdfSpending(
            @PathVariable(required = false) Long id
    ) throws IOException, DocumentException, TransformerException {
        //validation
        if(cashRegisterServiceImpl.checkCashRegister(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //action
        return cashRegisterServiceImpl.getPdfSpending(id);
    }

    @PostMapping("/deleteCashRegistersById")
    @ResponseBody
    public String deleteCashRegistersById(
            Long id
    ) throws JsonProcessingException {
        cashRegisterServiceImpl.deleteCashRegistersById(id);
        return mapper.writeValueAsString(null);
    }


    // others

    @GetMapping("/getCurrencies")
    @ResponseBody
    public String getCurrencies() throws JsonProcessingException {
        return mapper.writeValueAsString(internalCurrencyServiceImpl.findAll());
    }

    @GetMapping("/getManagers")
    @ResponseBody
    public String getManagers(
            Long managerId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(userServiceImpl.findManagers(managerId));
    }

    @GetMapping("/getDirector")
    @ResponseBody
    public String getDirector() throws JsonProcessingException {
        return mapper.writeValueAsString(userServiceImpl.findDirector());
    }

    @GetMapping("/getAgencies")
    @ResponseBody
    public String getAgencies(Long agencyId) throws JsonProcessingException {
        return mapper.writeValueAsString(agencyServiceImpl.findAllByDeletedFalseOrId(agencyId));
    }

    @GetMapping("/getRealtorsByAgencyId")
    @ResponseBody
    public String getRealtorsByAgencyId(
            Long agencyId,
            Long realtorId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(
                realtorServiceImpl.findAllByAgencyIdOrRealtorId(agencyId, realtorId));
    }

    @GetMapping("/getArticlesForIncome")
    @ResponseBody
    public String getArticlesForIncome() throws JsonProcessingException {
        List<Pair<Article, String>> articles = new ArrayList<>();
        articles.add(Pair.of(Article.FLAT_PAYMENT, Article.FLAT_PAYMENT.getValue()));
        articles.add(Pair.of(Article.OTHER_PAYMENT, Article.OTHER_PAYMENT.getValue()));
        return mapper.writeValueAsString(articles);
    }

    @GetMapping("/getArticlesForSpending")
    @ResponseBody
    public String getArticlesForSpending() throws JsonProcessingException {
        List<Pair<Article, String>> articles = new ArrayList<>();
        articles.add(Pair.of(Article.COMMISSION_AGENCIES, Article.COMMISSION_AGENCIES.getValue()));
        articles.add(Pair.of(Article.COMMISSION_MANAGER, Article.COMMISSION_MANAGER.getValue()));
        articles.add(Pair.of(Article.CONSTRUCTION_COSTS, Article.CONSTRUCTION_COSTS.getValue()));
        articles.add(Pair.of(Article.MONEY_FOR_DIRECTOR, Article.MONEY_FOR_DIRECTOR.getValue()));
        return mapper.writeValueAsString(articles);
    }

    @GetMapping("/getObjects")
    @ResponseBody
    public String getObjects(
            Long objectId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(objectServiceImpl.findAllDeletedFalseOrObjectId(objectId));
    }

    @GetMapping("/getFlatsWithContractWithFlatPaymentsByObjectId")
    @ResponseBody
    public String getFlatsWithContractWithFlatPaymentsByObjectId(
            Long id,
            Long flatId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatServiceImpl.getFlatsWithContractWithFlatPaymentByObjectId(id, flatId));
    }

    @GetMapping("/getFlatPaymentsByFlatId")
    @ResponseBody
    public String getFlatPaymentsByFlatId(
            Long id,
            Long flatPaymentId
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatPaymentServiceImpl.getAllByFlatIdPaidFalseAndDeletedFalse(id, flatPaymentId));
    }

    @GetMapping("/getFlatPaymentById")
    @ResponseBody
    public String getFlatPaymentById(
            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatPaymentServiceImpl.findById(id));
    }

    @GetMapping("/getFlatById")
    @ResponseBody
    public String getFlatById(
            Long id
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(flatServiceImpl.findById(id));
    }

    @PostMapping("/addRealtor")
    @ResponseBody
    public String addRealtor(
            @Valid @RequestBody RealtorDto realtorDto,
            BindingResult bindingResult
    ) throws IOException {
        //validation
        if (realtorServiceImpl.checkPhone(realtorDto.getPhone())) {
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
        realtorDto.setDirector(false);
        Realtor realtor = realtorServiceImpl.save(realtorDto.build(), realtorDto.getAgencyId());
        return mapper.writeValueAsString(realtor.getId());
    }

}
