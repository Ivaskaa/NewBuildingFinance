package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.BuyerTableDto;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.repository.BuyerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class BuyerService {
    private final BuyerRepository buyerRepository;

    public Page<BuyerTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection) {
        log.info("get buyer page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<BuyerTableDto> buyerPage = buyerRepository.findAll(pageable).map(Buyer::build);
        log.info("success");
        return buyerPage;
    }

//    public List<Buyer> findAll() {
//        List<Buyer> buyers = buyerRepository.findAll();
//        log.info("success");
//        return buyers;
//    }

    public Buyer save(Buyer buyer) {
        log.info("save buyer: {}", buyer);
        Buyer buyerAfterSave = buyerRepository.save(buyer);
        log.info("success");
        return buyerAfterSave;
    }

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

    public void deleteById(Long id) {
        log.info("delete buyer by id: {}", id);
        buyerRepository.deleteById(id);
        log.info("success");
    }

    public List<Buyer> findByName(String name) {
        log.info("get buyers by name: {}", name);
        List<Buyer> buyers = null;
        if(name.contains(" ") && name.split(" ").length > 1) {
            String[] words = name.split(" ");
            if (words.length == 2){
                buyers = buyerRepository.findByName(words[0], words[1]);
            }
        } else {
            name = name.replace(" ", "");
            buyers = buyerRepository.findByName(name);
        }
        log.info("success");
        return buyers;
    }

    public Buyer findById(Long id) {
        log.info("get buyer by id: {}", id);
        Buyer object = buyerRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }
}
