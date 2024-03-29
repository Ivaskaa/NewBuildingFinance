package com.example.NewBuildingFinance.service.flatPayment;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.service.flat.FlatService;
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
public class FlatPaymentServiceImpl implements FlatPaymentService{
    private final FlatPaymentRepository flatPaymentRepository;
    private final FlatService flatService;

    @Override
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
        Page<FlatPaymentTableDto> flats = flatPaymentRepository.findAllByFlatIdAndDeletedFalse(pageable, flatId).map(FlatPayment::build);
        log.info("success get flat payment page");
        return flats;
    }

    @Override
    public FlatPayment save(FlatPayment flatPayment) {
        log.info("save flatPayment: {}", flatPayment);
        FlatPayment flatPaymentAfterSave = flatPaymentRepository.save(flatPayment);
        log.info("success");
        return flatPaymentAfterSave;
    }

    @Override
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

    @Override
    public void deleteById(Long flatPaymentId) {
        log.info("delete flatPayment by id: {}", flatPaymentId);
        flatPaymentRepository.setDeleted(flatPaymentId);
        log.info("success");
    }

    @Override
    public FlatPayment findById(Long id) {
        log.info("get flatPayment by id: {}", id);
        FlatPayment flatPayment = flatPaymentRepository.findById(id).orElseThrow();
        log.info("success");
        return flatPayment;
    }

    @Override
    public boolean checkNumber(Integer number, Long flatId) {
        if (number != null) {
            FlatPayment flatPayment = flatPaymentRepository.findByNumberAndFlatIdAndDeletedFalse(number, flatId);
            return flatPayment != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkPlaned(Double planned, Long flatId) {
        if (planned != null){
            if (planned.equals(0d)){
                return false;
            }
            List<FlatPayment> flatPaymentList = flatPaymentRepository.findAllByFlatIdAndDeletedFalse(flatId);
            Flat flat = flatService.findById(flatId);
            Double flatPrice = flat.getSalePrice();
            for (FlatPayment flatPayment : flatPaymentList) {
                flatPrice -= flatPayment.getPlanned();
            }
            flatPrice -= planned;
            return flatPrice < 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkPlanedEdit(Long id, Double planned, Long flatId) {
        List<FlatPayment> flatPaymentList = flatPaymentRepository.findAllByFlatIdAndDeletedFalse(flatId);
        Flat flat = flatService.findById(flatId);
        Double flatPrice = flat.getSalePrice();
        for(FlatPayment flatPayment : flatPaymentList){
            if(!flatPayment.getId().equals(id)) {
                flatPrice -= flatPayment.getPlanned();
            }
        }
        flatPrice -= planned;
        return flatPrice < 0;
    }

    @Override
    public List<FlatPayment> getAllByFlatIdPaidFalseAndDeletedFalse(Long flatId, Long flatPaymentId) {
        List<FlatPayment> flatPayments;
        if(flatPaymentId == null) {
            log.info("get flat payments with paid false by flat id : {}", flatId);
            flatPayments = flatPaymentRepository.findAllByFlatIdAndPaidFalseAndDeletedFalse(flatId);
        } else {
            flatPayments = flatPaymentRepository.findAllByFlatIdAndPaidFalseAndDeletedFalseOrFlatIdAndId(flatId, flatId, flatPaymentId);
        }
        log.info("success get flat payments with paid false by flat id");
        return flatPayments;
    }
}
