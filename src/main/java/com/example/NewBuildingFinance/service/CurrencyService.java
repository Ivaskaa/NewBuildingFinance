package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.entities.currency.Currency;
import com.example.NewBuildingFinance.others.CurrencyJson;
import com.example.NewBuildingFinance.repository.CurrencyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public void saveCurrency(List<CurrencyJson> currencyJsons){
        log.info("save currency");
        for(CurrencyJson currencyJson : currencyJsons) {
            Currency currency = currencyRepository.findByName(currencyJson.getCc());
            currency.setName(currencyJson.getCc());
            currency.setPrice(currencyJson.getRate());
            log.info("save currency: {}", currency);
            currencyRepository.save(currency);
        }
        log.info("success save currency");
    }

    public List<Currency> getAll(){
        log.info("get all currency");
        List<Currency> currencies = currencyRepository.findAll();
        log.info("success get all currency");
        return currencies;
    }
}
