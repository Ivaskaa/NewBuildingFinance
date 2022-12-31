package com.example.NewBuildingFinance.service.buyer;

import com.example.NewBuildingFinance.dto.buyer.BuyerSaveDto;
import com.example.NewBuildingFinance.dto.buyer.BuyerTableDto;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.stripe.exception.StripeException;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface BuyerService {
    /**
     * find sorting page for buyer table from database
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @return page of buyer table dto
     */
    Page<BuyerTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection);

    /**
     * save new buyer in database
     * @param buyer buyer model
     * @return buyer after save
     */
    Buyer save(Buyer buyer) throws StripeException;

    /**
     * update old buyer in database
     * @param objectForm buyer model
     * @return buyer after save
     */
    Buyer update(Buyer objectForm);

    /**
     * delete buyer by id from database
     * @param id id for delete
     */
    void deleteById(Long id);

    /**
     * find buyer by id from database
     * @param id id for find
     * @return buyer
     */
    Buyer findById(Long id);

    /**
     * find buyer by name from database
     * @param name buyer name
     * @return buyer
     */
    List<Buyer> findByName(String name);


    /**
     * peassy password generate
     * https://www.baeldung.com/java-generate-secure-password
     * <a href="https://www.baeldung.com/java-generate-secure-password">...</a>
     * @return generated password
     */
    String generatePassword();

    /**
     * send email for buyer with buyer generated password
     * @param password password
     * @param email email for sending
     */
    void sendPasswordEmail(String password, String email);

    /**
     * validation without database request
     * phone validation
     * if contains "_" add error to bindingResult
     * @param bindingResult binding result for validation
     * @param phone phone
     */
    void phoneValidation(BindingResult bindingResult, String phone);

    /**
     * validation with database request
     * email unique validation
     * if not unique add error to bindingResult
     * @param bindingResult binding result for validation
     * @param email email
     */
    void emailValidation(BindingResult bindingResult, String email, Long id);

    /**
     * buyer document validation (id card, passport)
     * without database request
     * @param bindingResult binding result for exeption
     * @param buyerSaveDto buyer dto
     */
    void documentValidation(BindingResult bindingResult, BuyerSaveDto buyerSaveDto);
}

