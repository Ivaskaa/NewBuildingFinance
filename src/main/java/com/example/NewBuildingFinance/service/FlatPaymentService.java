package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
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
public class FlatPaymentService {
    private final FlatPaymentRepository flatPaymentRepository;
    private final FlatService flatService;

    public Page<FlatPaymentTableDto> findPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long flatId
    ) {
        log.info("get flat payment page. page: {}, size: {} field: {}, direction: {}, flatId: {}",
                currentPage - 1, size, sortingField, sortingDirection, flatId);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<FlatPaymentTableDto> flats = flatPaymentRepository.findAllByFlatId(pageable, flatId).map(FlatPayment::build);
        log.info("success");
        return flats;
    }

    public FlatPayment save(FlatPayment flatPayment) {
        log.info("save flatPayment: {}", flatPayment);
        FlatPayment flatPaymentAfterSave = flatPaymentRepository.save(flatPayment);
        log.info("success");
        return flatPaymentAfterSave;
    }

    public FlatPayment update(FlatPayment flatPaymentForm) {
        log.info("update flatPayment: {}", flatPaymentForm);
        FlatPayment object = flatPaymentRepository.findById(flatPaymentForm.getId()).orElseThrow();
        object.setNumber(flatPaymentForm.getNumber());
        object.setDate(flatPaymentForm.getDate());
        object.setPlanned(flatPaymentForm.getPlanned());
        object.setActually(flatPaymentForm.getActually());
        object.setFlat(flatPaymentForm.getFlat());
        flatPaymentRepository.save(object);
        log.info("success");
        return object;
    }

    public void deleteById(Long id) {
        log.info("delete flatPayment by id: {}", id);
        flatPaymentRepository.deleteById(id);
        log.info("success");
    }

    public FlatPayment findById(Long id) {
        log.info("get flatPayment by id: {}", id);
        FlatPayment flatPayment = flatPaymentRepository.findById(id).orElseThrow();
        log.info("success");
        return flatPayment;
    }

    public boolean checkNumber(Long number, Long flatId) {
        FlatPayment flatPayment = flatPaymentRepository.findByNumberAndFlatId(number, flatId);
        return flatPayment != null;
    }

    public boolean checkPlaned(Integer planned, Long flatId) {
        List<FlatPayment> flatPaymentList = flatPaymentRepository.findAllByFlatId(flatId);
        Flat flat = flatService.findById(flatId);
        Integer flatPrice = flat.getSalePrice();
        for(FlatPayment flatPayment : flatPaymentList){
            flatPrice -= flatPayment.getPlanned();
        }
        flatPrice -= planned;
        return flatPrice < 0;
    }

    public boolean checkPlanedEdit(Long id, Integer planned, Long flatId) {
        List<FlatPayment> flatPaymentList = flatPaymentRepository.findAllByFlatId(flatId);
        Flat flat = flatService.findById(flatId);
        Integer flatPrice = flat.getSalePrice();
        for(FlatPayment flatPayment : flatPaymentList){
            if(!flatPayment.getId().equals(id)) {
                flatPrice -= flatPayment.getPlanned();
            }
        }
        flatPrice -= planned;
        return flatPrice < 0;
    }
}
