package com.example.NewBuildingFinance.controllers.contracts;

import com.example.NewBuildingFinance.dto.contract.ContractSaveDto;
import com.example.NewBuildingFinance.dto.contract.ContractUploadDto;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.service.*;
import com.example.NewBuildingFinance.service.auth.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/contracts")
public class ContractController {
    private final UserService userService;
    private final BuyerService buyerService;
    private final InternalCurrencyService currencyService;
    private final ContractService contractService;
    private final ContractTemplateService contractTemplateService;
    private final FlatService flatService;
    private final ObjectService objectService;
    private final ObjectMapper mapper;
    private static final String PDF_OUTPUT = "src/main/resources/html2pdf.pdf";

    @GetMapping()
    public String contracts(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("objects", objectService.findAll());
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("user", user);
        return "contract/contracts";
    }

    @GetMapping("/contract/{id}")
    public String contractFromFlat(
            @PathVariable(required = false) Long id,
            Long flatId,
            Model model
    ) {
        System.out.println(flatId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        model.addAttribute("currencies", currencyService.findAll());
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

            Optional<Long> id,
            Optional<String> dateStart,
            Optional<String> dateFin,
            Optional<Integer> flatNumber,
            Optional<Long> objectId,
            Optional<String> buyerName,
            Optional<String> comment
    ) throws JsonProcessingException {
        return mapper.writeValueAsString(contractService.findSortingAndSpecificationPage(
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
        if(contractService.checkRealtor(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "No selected realtor for this flat"));
        }
        if(contractService.checkFlatPayments(contractSaveDto.getFlatId())){
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
        ContractUploadDto contract = contractService.save(contractSaveDto);
        return mapper.writeValueAsString(contract);
    }

    @PostMapping("/updateContract")
    @ResponseBody
    public String updateContract(
            @Valid @RequestBody ContractSaveDto contractSaveDto,
            BindingResult bindingResult
    ) throws IOException, ParseException {
        //validation
        if(contractService.checkStatus(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "Flat status must be active"));
        }
        if(contractService.checkRealtor(contractSaveDto.getFlatId())){
            bindingResult.addError(new FieldError("contractSaveDto", "flatId", "No selected realtor for this flat"));
        }
        if(contractService.checkFlatPayments(contractSaveDto.getFlatId())){
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
        contractService.update(contractSaveDto);
        return mapper.writeValueAsString(null);
    }

    @GetMapping("/getContractById")
    @ResponseBody
    public String getContractById(
            Long id
    ) throws JsonProcessingException {
        ContractUploadDto contract = contractService.findById(id);
        return mapper.writeValueAsString(contract);
    }

//    @PostMapping("/createPdf")
//    @RequestMapping(value = "/createPdf", method = RequestMethod.POST)
//    public void createPdf(
//            HttpServletResponse response,
//            Long id
//    ) throws Exception {
//        contractService.exportPdf(response, id);
//    }


    @GetMapping("/getPdf/{id}")
    public ResponseEntity<byte[]> getPdf(
            @PathVariable(required = false) Long id
    ) throws Exception {
        //validation
        if(contractService.checkContract(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //action
        ResponseEntity<byte[]> response = contractService.getPdf(id);
        return response;
    }



    @PostMapping("/deleteContractById")
    @ResponseBody
    public String deleteContractById(
            Long id
    ) throws JsonProcessingException {
        contractService.deleteById(id);
        return mapper.writeValueAsString(null);
    }

    // others

    // for selects

    @GetMapping("/getAllOnSaleObjects")
    @ResponseBody
    public String getObjects() throws JsonProcessingException {
        List<Object> objects = objectService.findAllOnSale();
        return mapper.writeValueAsString(objects);
    }

    @GetMapping("/getFlatsWithoutContractByObjectId")
    @ResponseBody
    public String getFlatsWithoutContractByObjectId(
            Long id,
            Long flatId
    ) throws JsonProcessingException {
        List<Flat> flats = flatService.getFlatsWithoutContractByObjectId(id, flatId);
        return mapper.writeValueAsString(flats);
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
        List<Pair<ContractStatus, String>> list = new ArrayList<>();
        for(ContractStatus contractStatus : ContractStatus.values()){
            list.add(Pair.of(contractStatus, contractStatus.getValue()));
        }
        return mapper.writeValueAsString(list);
    }

    @GetMapping("/getContractTemplates")
    @ResponseBody
    public String getContractTemplates() throws JsonProcessingException {
        List<ContractTemplate> contractTemplates = contractTemplateService.findAll();
        return mapper.writeValueAsString(contractTemplates);
    }

    // for uploads info

    @GetMapping("/getBuyerById")
    @ResponseBody
    public String getBuyerById(
            Long id
    ) throws JsonProcessingException {
        Buyer buyer = buyerService.findById(id);
        return mapper.writeValueAsString(buyer);
    }

    @GetMapping("/getFlatById")
    @ResponseBody
    public String getFlatById(
            Long id
    ) throws JsonProcessingException {
        Flat flat = flatService.findById(id);
        return mapper.writeValueAsString(flat);
    }


//    @GetMapping("/getBuyerById")
//    @ResponseBody
//    public String getBuyerById(
//            Long id
//    ) throws JsonProcessingException {
//        Buyer buyer = buyerService.findById(id);
//        return mapper.writeValueAsString(buyer);
//    }

}
