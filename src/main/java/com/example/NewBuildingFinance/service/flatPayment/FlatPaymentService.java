package com.example.NewBuildingFinance.service.flatPayment;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentTableDto;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FlatPaymentService {
    /**
     * sorting page for flat payment table by flat id
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param flatId flat id
     * @return page of flat table dto
     */
    Page<FlatPaymentTableDto> findPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long flatId
    );

    /**
     * save flat payment
     * @param flatPayment flat payment
     * @return flat payment
     */
    FlatPayment save(FlatPayment flatPayment);

    /**
     * update flat payment
     * @param flatPaymentForm flat payment
     * @return flat payment
     */
    FlatPayment update(FlatPayment flatPaymentForm);

    /**
     * set deleted true by id
     * @param id flat payment id
     */
    void deleteById(Long id);

    /**
     * find flat payment by id
     * @param id id
     * @return flat payment
     */
    FlatPayment findById(Long id);

    /**
     * validation equal number
     * with database request
     * @param number number
     * @param flatId flat id
     * @return true if validation success
     */
    boolean checkNumber(Integer number, Long flatId);

    /**
     * validation planed sum
     * * with database request
     * @param planned planed
     * @param flatId flat id
     * @return true if validation success
     */
    boolean checkPlaned(Double planned, Long flatId);

    /**
     * validation planed for update
     * with database request
     * @param id planed
     * @param planned planed
     * @param flatId flat id
     * @return true if validation success
     */
    boolean checkPlanedEdit(Long id, Double planned, Long flatId);

    /**
     * get all by flat payment id paid false and deleted false
     * @param flatId flat id
     * @param flatPaymentId flat payment id
     * @return flat payment list
     */
    List<FlatPayment> getAllByFlatIdPaidFalseAndDeletedFalse(Long flatId, Long flatPaymentId);
}
