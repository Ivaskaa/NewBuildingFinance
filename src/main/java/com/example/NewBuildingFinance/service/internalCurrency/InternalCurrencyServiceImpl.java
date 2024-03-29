package com.example.NewBuildingFinance.service.internalCurrency;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.repository.InternalCurrencyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class InternalCurrencyServiceImpl implements InternalCurrencyService{
    private final InternalCurrencyRepository internalCurrencyRepository;

    @Override
    public List<InternalCurrency> findAll() {
        log.info("get all currency");
        List<InternalCurrency> currencyList = internalCurrencyRepository.findAll();
        log.info("success");
        return currencyList;
    }

    @Override
    public InternalCurrency update(InternalCurrency objectForm) {
        log.info("update currency: {}", objectForm);
        InternalCurrency object = internalCurrencyRepository.findById(objectForm.getId()).orElseThrow();
        object.setCashRegister(objectForm.getCashRegister());
        object.setPrice(objectForm.getPrice());
        internalCurrencyRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public InternalCurrency findById(Long id) {
        log.info("get currency by id: {}", id);
        InternalCurrency object = internalCurrencyRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }
}
