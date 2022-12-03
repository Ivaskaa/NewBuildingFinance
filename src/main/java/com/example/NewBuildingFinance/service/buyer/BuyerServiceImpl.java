package com.example.NewBuildingFinance.service.buyer;

import com.example.NewBuildingFinance.dto.buyer.BuyerSaveDto;
import com.example.NewBuildingFinance.dto.buyer.BuyerTableDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.buyer.DocumentStyle;
import com.example.NewBuildingFinance.repository.BuyerRepository;
import com.example.NewBuildingFinance.service.staticService.StaticServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class BuyerServiceImpl implements BuyerService{
    private final BuyerRepository buyerRepository;
    private final StaticServiceImpl staticService;

    @Override
    public Page<BuyerTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection) {
        log.info("get buyer page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort;
        if(sortingField.contains(" and ")) {
            sort = staticService.sort(sortingField, sortingDirection);
        } else {
            sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        }
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<BuyerTableDto> buyerPage = buyerRepository.findAllByDeletedFalse(pageable).map(Buyer::build);
        log.info("success");
        return buyerPage;
    }

    @Override
    public Buyer save(Buyer buyer) {
        log.info("save buyer: {}", buyer);
        System.out.println();
        Buyer buyerAfterSave = buyerRepository.save(buyer);
        log.info("success");
        return buyerAfterSave;
    }

    @Override
    public Buyer update(Buyer objectForm) {
        log.info("update buyer: {}", objectForm);
        Buyer object = buyerRepository.findById(objectForm.getId()).orElseThrow();
        object.setName(objectForm.getName());
        object.setSurname(objectForm.getSurname());
        object.setLastname(objectForm.getLastname());
        object.setAddress(objectForm.getAddress());
        object.setIdNumber(objectForm.getIdNumber());
        object.setPassportSeries(objectForm.getPassportSeries());
        object.setPassportNumber(objectForm.getPassportNumber());
        object.setPassportWhoIssued(objectForm.getPassportWhoIssued());
        object.setPhone(objectForm.getPhone());
        object.setEmail(objectForm.getEmail());
        object.setNote(objectForm.getNote());
        object.setRealtor(objectForm.getRealtor());
        buyerRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public void deleteById(Long buyerId) {
        log.info("set buyer.delete true by id: {}", buyerId);
        buyerRepository.setDeleted(buyerId);
        log.info("success set buyer.delete true");
    }

    @Override
    public List<Buyer> findByName(String name) {
        log.info("get buyers by name: {}", name);
        List<Buyer> buyers = null;
        if(name.contains(" ") && name.split(" ").length > 1) {
            String[] words = name.split(" ");
            if (words.length == 2){
                buyers = buyerRepository.findByNameAndDeletedFalse(words[0], words[1]);
            }
        } else {
            name = name.replace(" ", "");
            buyers = buyerRepository.findByNameAndDeletedFalse(name);
        }
        log.info("success");
        return buyers;
    }

    @Override
    public Buyer findById(Long id) {
        log.info("get buyer by id: {}", id);
        Buyer object = buyerRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    public void documentValidation(BindingResult bindingResult, BuyerSaveDto buyerSaveDto) {
        if(buyerSaveDto.getDocumentStyle() == null || buyerSaveDto.getDocumentStyle().equals("")){
            return;
        }
        DocumentStyle documentStyle = null;
        try {
            documentStyle = DocumentStyle.valueOf(buyerSaveDto.getDocumentStyle());
        } catch (Exception exception){
            exception.printStackTrace();
        }
        if(documentStyle != null) {
            if (documentStyle.equals(DocumentStyle.ID_CARD)) {
                if (buyerSaveDto.getIdCardNumber() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardNumber", "Must not be empty"));
                } else if (buyerSaveDto.getIdCardNumber() > 9999999999999L || buyerSaveDto.getIdCardNumber() <= 999999999999L) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardNumber", "Must be entered 13 numbers"));
                }
                if (buyerSaveDto.getIdCardWhoIssued() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardWhoIssued", "Must not be empty"));
                } else if (buyerSaveDto.getIdCardWhoIssued() > 9999 || buyerSaveDto.getIdCardWhoIssued() <= 999) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "idCardWhoIssued", "Must be entered 4 numbers"));
                }
            } else if (documentStyle.equals(DocumentStyle.PASSPORT)) {
                if (buyerSaveDto.getPassportSeries() == null || buyerSaveDto.getPassportSeries().equals("")) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportSeries", "Must not be empty"));
                } else if (buyerSaveDto.getPassportSeries().length() != 2) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportSeries", "Must be entered 2 letters"));
                }
                if (buyerSaveDto.getPassportNumber() == null) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportNumber", "Must not be empty"));
                } else if (buyerSaveDto.getPassportNumber() > 999999 || buyerSaveDto.getPassportNumber() <= 99999) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportNumber", "Must be entered 6 numbers"));
                }
                if (buyerSaveDto.getPassportWhoIssued() == null || buyerSaveDto.getPassportWhoIssued().equals("")) {
                    bindingResult.addError(new FieldError("buyerSaveDto", "passportWhoIssued", "Must not be empty"));
                }
            }
        }
    }

    public void phoneValidation(BindingResult bindingResult, String phone) {
        if(phone.contains("_")){
            bindingResult.addError(new FieldError("buyerSaveDto", "phone", "Phone must be valid"));
        }
    }
}
