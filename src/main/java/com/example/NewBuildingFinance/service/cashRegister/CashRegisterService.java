package com.example.NewBuildingFinance.service.cashRegister;

import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDto;
import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDtoForFlat;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeUploadDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingUploadDto;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.itextpdf.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface CashRegisterService {

    /**
     * sorting specification request for cash register table
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param number search cash register number
     * @param dateStart search cash register date start
     * @param dateFin search cash register date end
     * @param economic search cash register economic
     * @param status search cash register  status
     * @param objectId search cash register object
     * @param article search cash register article
     * @param priceStart search cash register price start
     * @param priceFin search cash register price final
     * @param currencyId search cash register currencyId
     * @param counterparty search cash register counterparty
     * @return page of cash register table dto
     * @throws ParseException dateStart and dateFin exception
     */
    Page<CashRegisterTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

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
    ) throws ParseException;

    /**
     * get cash registers for table in flat page
     * @param id id for search
     * @return list og cash register table dto for flat
     */
    List<CashRegisterTableDtoForFlat> getCashRegistersByFlatId(Long id);

    /**
     * save new income(cash register)
     * @param income income model
     * @return income after save
     */
    CashRegister saveIncome(CashRegister income);

    /**
     * update old income(cash register)
     * @param income income model
     * @return income after save
     */
    IncomeUploadDto updateIncome(CashRegister income);

    /**
     * save new spending(cash register)
     * @param spending spending model
     * @return spending after save
     */
    CashRegister saveSpending(CashRegister spending);

    /**
     * update old spending(cash register)
     * @param spending spending model
     * @return spending after save
     */
    SpendingUploadDto updateSpending(CashRegister spending);

    /**
     * delete income(cash register) by id
     * @param id id for delete
     */
    void deleteIncomeById(Long id);

    /**
     * delete spending(cash register) by id
     * @param id id for delete
     */
    void deleteSpendingById(Long id);

    /**
     * find income(cash register) by id
     * @param id id for find
     * @return income object
     */
    IncomeUploadDto findIncomeById(Long id);

    /**
     * find spending(cash register) by id
     * @param id id for find
     * @return spending object
     */
    SpendingUploadDto findSpendingById(Long id);

    /**
     * find cash register by id
     * @param id id for find
     * @return cash register object
     */
    CashRegister findById(Long id);

    /**
     * find income by flat payment id
     * @param flatPaymentId flat payment id for find
     * @return cash register object
     */
    CashRegister findIncomeByFlatPaymentId(Long flatPaymentId);

    /**
     * find spending by flat id
     * @param flatId flat payment id for find
     * @return cash register object
     */
    CashRegister findSpendingByFlatId(Long flatId);

    /**
     * create pdf file for income
     * @param id id for find
     * @return response with pdf file
     * @throws IOException if file not fund
     * @throws DocumentException if exception with create pdf
     */
    ResponseEntity<byte[]> getPdfIncome(Long id) throws IOException, DocumentException;

    /**
     * create pdf file for spending
     * @param id id for find
     * @return response with pdf file
     * @throws IOException if file not fund
     * @throws DocumentException if exception with create pdf
     */
    ResponseEntity<byte[]> getPdfSpending(Long id) throws IOException, DocumentException;

    /**
     * in database check unique cash register number
     * @param number cash register number
     * @return if number is exist return true
     */
    boolean checkNumber(Long number);

    /**
     * in database check cash register
     * @param id cash register id
     * @return if cash register exists return true
     */
    boolean checkCashRegister(Long id);
}
