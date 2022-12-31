package com.example.NewBuildingFinance.service.contract;

import com.example.NewBuildingFinance.dto.cashRegister.CommissionTableDtoForAgency;
import com.example.NewBuildingFinance.dto.contract.*;
import com.example.NewBuildingFinance.entities.contract.Contract;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.text.ParseException;

public interface ContractService {
    /**
     * sorting specification page for contract table
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param id contract id
     * @param dateStart contract date start
     * @param dateFin contract date fin
     * @param flatNumber contract flat number
     * @param objectId contract object id
     * @param buyerName contract buyer name
     * @param comment contract comment
     * @return page of contract table dto
     * @throws ParseException dateStart and dateFin exception
     */
    Page<ContractTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long id,
            String dateStart,
            String dateFin,
            Integer flatNumber,
            Long objectId,
            String buyerName,
            String comment
    ) throws ParseException;

    /**
     * sorting page for contract table for buyer
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param buyerId buyer id
     * @return page of contract table dto for buyer
     */
    Page<ContractTableDtoForBuyers> findSortingPageByBuyerId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long buyerId
    );

    /**
     * sorting page for contract table for agency
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param agencyId agency id
     * @return page of contract table dto for agency
     */
    Page<ContractTableDtoForAgency> findSortingContractsPageByAgencyId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long agencyId
    );

    Page<CommissionTableDtoForAgency> findSortingCommissionsPageByAgencyId(
            Integer page,
            Integer size,
            String field,
            String direction,
            Long agencyId
    );

    /**
     * save new contract
     * @param contractSaveDto contract save dto
     * @return contract upload dto after save
     */
    ContractUploadDto save(ContractSaveDto contractSaveDto) throws ParseException;

    /**
     * update old contract
     * @param contractSaveDto contract save dto
     * @return contract upload dto after save
     */
    ContractUploadDto update(ContractSaveDto contractSaveDto) throws ParseException;

    /**
     * delete contract by id
     * flat contract = null
     * flat status = ACTIVE
     * @param contractId contract id for delete
     */
    void deleteById(Long contractId);

    /**
     * find contract upload dto by id
     * @param id contract id
     * @return contract upload dto
     */
    ContractUploadDto findById(Long id);

    /**
     * create pdf file for contract
     * @param id contract id
     * @return response with pdf file
     * @throws IOException if file not found
     */
    ResponseEntity<byte[]> getPdf(Long id) throws IOException;

    /**
     * check presence of realtor in flat
     * @param flatId flat id
     * @return if flat has a realtor return false, if flatId == null return false
     */
    boolean checkRealtor(Long flatId);

    /**
     * check that full planned payment is equal to sale price
     * @param flatId flat id
     * @return if full planned payment equal to sale price return false, if flatId == null return false
     */
    boolean checkFlatPayments(Long flatId);

    /**
     * check contract exists
     * @param id contract id
     * @return if contract exist return false
     */
    boolean checkContract(Long id);

    /**
     * buyer document validation (id card, passport)
     * without database request
     * @param bindingResult binding result for exeption
     * @param contractSaveDto contract dto
     */
    void documentValidation(BindingResult bindingResult, ContractSaveDto contractSaveDto);
}
