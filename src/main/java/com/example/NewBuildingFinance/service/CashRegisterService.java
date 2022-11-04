package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.cashRegister.CashRegisterTableDto;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeSaveDto;
import com.example.NewBuildingFinance.dto.cashRegister.IncomeUploadDto;
import com.example.NewBuildingFinance.dto.cashRegister.SpendingUploadDto;
import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.others.specifications.CashRegisterSpecification;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.ParseException;

@Service
@Log4j2
@AllArgsConstructor
public class CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;
    private final FlatPaymentRepository flatPaymentRepository;

    public Page<CashRegisterTableDto> findSortingAndSpecificationPage(
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
            Double price,
            Long currencyId,
            String counterparty
    ) throws ParseException {
        log.info("get cash register page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<CashRegister> specification = Specification
                .where(CashRegisterSpecification.likeNumber(number))
                .and(CashRegisterSpecification.likeDate(dateStart, dateFin))
                .and(CashRegisterSpecification.likeEconomic(economic))
                .and(CashRegisterSpecification.likeStatus(status))
                .and(CashRegisterSpecification.likeObjectId(objectId))
                .and(CashRegisterSpecification.likeArticle(article))
                .and(CashRegisterSpecification.likePrice(price))
                .and(CashRegisterSpecification.likeCurrencyId(currencyId))
                .and(CashRegisterSpecification.likeCounterparty(counterparty));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<CashRegisterTableDto> cashRegisterPage = cashRegisterRepository.findAll(specification, pageable).map(CashRegister::build);
        log.info("success get cash register page");
        return cashRegisterPage;
    }

    public IncomeUploadDto saveIncome(CashRegister income) {
        log.info("save income: {}", income);
        CashRegister cashRegister = cashRegisterRepository.save(income);
        if (income.getArticle().equals(Article.FLAT_PAYMENT)) {
            FlatPayment flatPayment = flatPaymentRepository.findById(cashRegister.getFlatPayment().getId()).orElse(null);
            if (flatPayment != null) {
                System.out.println(flatPayment.getFlat().getBuyer().getSurname());
                cashRegister.setCounterparty(
                        flatPayment.getFlat().getBuyer().getSurname() + " " +
                        flatPayment.getFlat().getBuyer().getName());
                flatPayment.setActually(cashRegister.getPrice());
                flatPayment.setPaid(true);
                flatPaymentRepository.save(flatPayment);
            }
        }
        log.info("success save income");
        return cashRegister.buildIncomeUploadDto();
    }

    public IncomeUploadDto updateIncome(CashRegister income) {
        log.info("update income: {}", income);
        CashRegister cashRegister = cashRegisterRepository.findById(income.getId()).orElseThrow();
        Article article = cashRegister.getArticle();
        FlatPayment oldFlatPayment = cashRegister.getFlatPayment();
        CashRegister cashRegisterAfterSave = cashRegisterRepository.save(income);
        if (income.getArticle().equals(Article.FLAT_PAYMENT)) {
            FlatPayment newFlatPayment = flatPaymentRepository.findById(cashRegisterAfterSave.getFlatPayment().getId()).orElse(null);
            if (newFlatPayment != null) {
                if(!cashRegister.getFlatPayment().equals(newFlatPayment)){
                    if (oldFlatPayment != null) {
                        oldFlatPayment.setActually(null);
                        oldFlatPayment.setPaid(false);
                        flatPaymentRepository.save(oldFlatPayment);
                    }
                }
                cashRegisterAfterSave.setCounterparty(
                        newFlatPayment.getFlat().getBuyer().getSurname() + " " +
                                newFlatPayment.getFlat().getBuyer().getName());
                newFlatPayment.setActually(cashRegisterAfterSave.getPrice());
                newFlatPayment.setPaid(true);
                flatPaymentRepository.save(newFlatPayment);
            }
        }
        if (!article.equals(cashRegisterAfterSave.getArticle()) && cashRegisterAfterSave.getArticle().equals(Article.OTHER_PAYMENT)){
            if (oldFlatPayment != null) {
                oldFlatPayment.setActually(null);
                oldFlatPayment.setPaid(false);
                flatPaymentRepository.save(oldFlatPayment);
            }
        }
        log.info("success update income");
        return cashRegisterAfterSave.buildIncomeUploadDto();
    }

    public SpendingUploadDto saveSpending(CashRegister spending) {
        log.info("save spending: {}", spending);
//        if(spending.getArticle().equals(Art))
//        CashRegister cashRegister = cashRegisterRepository.save(income); // fixme
//
//
//        log.info("success save spending");
//        return cashRegisterAfterSave.buildIncomeUploadDto();
        return null;
    }

    public void deleteIncomeById(Long id) {
        log.info("delete income by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        if (cashRegister.getArticle().equals(Article.FLAT_PAYMENT)){
            cashRegister.setFlatPayment(null);
            cashRegister.setObject(null);
            FlatPayment oldFlatPayment = cashRegister.getFlatPayment();
            if (oldFlatPayment != null) {
                oldFlatPayment.setActually(null);
                oldFlatPayment.setPaid(false);
                flatPaymentRepository.save(oldFlatPayment);
            }
        }
        cashRegister.setDeleted(true);
        cashRegisterRepository.save(cashRegister);
        log.info("success delete income by id");
    }

    public IncomeUploadDto findIncomeById(Long id) {
        log.info("get income by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        IncomeUploadDto cashRegisterIncomeUploadDto = cashRegister.buildIncomeUploadDto();
        log.info("success get income by id");
        return cashRegisterIncomeUploadDto;
    }

    public SpendingUploadDto findSpendingById(Long id) {
        log.info("get spending by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        SpendingUploadDto cashRegisterSpendingUploadDto = cashRegister.buildSpendingUploadDto();
        log.info("success get spending by id");
        return cashRegisterSpendingUploadDto;
    }

    public CashRegister findById(Long id) {
        log.info("get cash register by id: {}", id);
        CashRegister cashRegister = cashRegisterRepository.findById(id).orElseThrow();
        log.info("success get cash register by id");
        return cashRegister;
    }

    public CashRegister findIncomeByFlatPaymentId(Long flatPaymentId) {
        log.info("get income by flat payment id: {}", flatPaymentId);
        CashRegister cashRegister = cashRegisterRepository.findByFlatPaymentId(flatPaymentId).orElse(null);
        log.info("success get income by flat payment id");
        return cashRegister;
    }

    public CashRegister findSpendingByFlatId(Long flatId) {
        log.info("get spending by flat id: {}", flatId);
        CashRegister cashRegister = cashRegisterRepository.findByFlatId(flatId).orElse(null);
        log.info("success get spending by flat id");
        return cashRegister;
    }

    public boolean checkNumber(Long number) {
        CashRegister cashRegister = cashRegisterRepository.findByNumber(number);
        return cashRegister != null;
    }
}
