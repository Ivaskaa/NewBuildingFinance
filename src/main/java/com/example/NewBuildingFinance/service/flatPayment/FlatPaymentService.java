package com.example.NewBuildingFinance.service.flatPayment;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentTableDto;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FlatPaymentService {
    Page<FlatPaymentTableDto> findPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long flatId
    );

    FlatPayment save(FlatPayment flatPayment);

    FlatPayment update(FlatPayment flatPaymentForm);

    void deleteById(Long id);

    FlatPayment findById(Long id);

    boolean checkNumber(Integer number, Long flatId);

    boolean checkPlaned(Double planned, Long flatId);

    boolean checkPlanedEdit(Long id, Double planned, Long flatId);

    List<FlatPayment> getAllByFlatIdPaidFalseAndDeletedFalse(Long flatId, Long flatPaymentId);
}
