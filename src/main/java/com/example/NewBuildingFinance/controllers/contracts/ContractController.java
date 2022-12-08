package com.example.NewBuildingFinance.controllers.contracts;

import com.example.NewBuildingFinance.dto.contract.ContractSaveDto;
import com.example.NewBuildingFinance.dto.contract.ContractUploadDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.service.auth.user.UserServiceImpl;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import com.example.NewBuildingFinance.service.contract.ContractServiceImpl;
import com.example.NewBuildingFinance.service.contractTemplate.ContractTemplateServiceImpl;
import com.example.NewBuildingFinance.service.flat.FlatServiceImpl;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.example.NewBuildingFinance.service.object.ObjectServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/contracts")
public class ContractController {
    private final UserServiceImpl userServiceImpl;
    private final BuyerServiceImpl buyerServiceImpl;
    private final InternalCurrencyServiceImpl internalCurrencyServiceImpl;
    private final ContractServiceImpl contractServiceImpl;
    private final ContractTemplateServiceImpl contractTemplateServiceImpl;
    private final FlatServiceImpl flatServiceImpl;
    private final ObjectServiceImpl objectServiceImpl;
    private final ObjectMapper mapper;

    @GetMapping()
    public String contracts(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectServiceImpl.findAll());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());
        model.addAttribute("user", user);
        return "contract/contracts";
    }

    @GetMapping("/contract/{id}")
    public String contractFromFlat(
            @PathVariable(required = false) Long id,
            Long flatId,
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userServiceImpl.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", internalCurrencyServiceImpl.findAll());
        model.addAttribute("contractId", id);
        model.addAttribute("flatId", Objects.requireNonNullElse(flatId, 0));
        model.addAttribute("user", user);
        return "contract/contract";
    }

    @GetMapping("/getContracts")
    @ResponseBody
    public String getContracts(
            Integer page,
            Integer size,
            String field,
            String direction,

            Long id,
            String dateStart,
            String dateFin,
            Integer flatNumber,
            Long objectId,
            String buyerName,
            String comment
    ) throws JsonProcessingException, ParseException {
        return mapper.writeValueAsString(contractServiceImpl.findSortingAndSpecificationPage(
                page, size, field, direction,
                id,
                dateStart, dateFin,
                flatNumber,
                objectId,
                buyerName,
                comment));
    }

    @PostMapping("/addContract")
    @ResponseBody
    public String addContract(
            @Valid @RequestBody ContractSaveDto contractSaveDto,
            BindingResult bindingResult
    ) throws IOException, ParseException {
        //validation
        contractServiceImpl.checkDocument(bindingResult, contractSaveDto);
        if(contractServiceImpl.checkRealtor(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "No selected realtor for this flat"));
        }
        if(contractServiceImpl.checkFlatPayments(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "The payment of flat not full planned"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }
        //action
        ContractUploadDto contract = contractServiceImpl.save(contractSaveDto);
        return mapper.writeValueAsString(contract);
    }

    @PostMapping("/updateContract")
    @ResponseBody
    public String updateContract(
            @Valid @RequestBody ContractSaveDto contractSaveDto,
            BindingResult bindingResult
    ) throws IOException, ParseException {
        //validation
        contractServiceImpl.checkDocument(bindingResult, contractSaveDto);
        if(contractServiceImpl.checkRealtor(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "No selected realtor for this flat"));
        }
        if(contractServiceImpl.checkFlatPayments(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "The payment of flat not full planned"));
        }
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return mapper.writeValueAsString(errors);
        }

        //action
        contractServiceImpl.update(contractSaveDto);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getContractById")
    @ResponseBody
    public String getContractById(
            Long id
    ) throws JsonProcessingException {
        ContractUploadDto contract = contractServiceImpl.findById(id);
        return mapper.writeValueAsString(contract);
    }

    @GetMapping("/getPdf/{id}")
    public ResponseEntity<byte[]> getPdf(
            @PathVariable(required = false) Long id
    ) throws Exception {
        //validation
        if(contractServiceImpl.checkContract(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //action
        ResponseEntity<byte[]> response = contractServiceImpl.getPdf(id);
        return response;
    }



    @PostMapping("/deleteContractById")
    @ResponseBody
    public String deleteContractById(
            Long id
    ) throws JsonProcessingException {
        contractServiceImpl.deleteById(id);
        return mapper.writeValueAsString(null);
    }

    // others

    // for selects

    @GetMapping("/getAllOnSaleObjects")
    @ResponseBody
    public String getObjects(Long objectId) throws JsonProcessingException {
        List<Object> objects = objectServiceImpl.findAllOnSaleOrObjectId(objectId);
        return mapper.writeValueAsString(objects);
    }

    @GetMapping("/getFlatsWithoutContractByObjectId")
    @ResponseBody
    public String getFlatsWithoutContractByObjectId(
            Long id,
            Long flatId
    ) throws JsonProcessingException {
        List<Flat> flats = flatServiceImpl.getFlatsWithoutContractByObjectId(id, flatId);
        return mapper.writeValueAsString(flats);
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
    public String getStatuses() throws JsonProcessingException {
        List<Pair<ContractStatus, String>> list = new ArrayList<>();
        for(ContractStatus contractStatus : ContractStatus.values()){
            list.add(Pair.of(contractStatus, contractStatus.getValue()));
        }
        return mapper.writeValueAsString(list);
    }

    @GetMapping("/getContractTemplates")
    @ResponseBody
    public String getContractTemplates(
            Long contractTemplateId
    ) throws JsonProcessingException {
        List<ContractTemplate> contractTemplates = contractTemplateServiceImpl.findAllDeletedFalseOrById(contractTemplateId);
        return mapper.writeValueAsString(contractTemplates);
    }

    // for uploads info

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerServiceImpl.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @GetMapping("/getFlatById")
    @ResponseBody
    public String getFlatById(
            Long id
    ) throws JsonProcessingException {
        Flat flat = flatServiceImpl.findById(id);
        return mapper.writeValueAsString(flat);
    }

}
