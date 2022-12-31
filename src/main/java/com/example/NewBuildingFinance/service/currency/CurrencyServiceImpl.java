package com.example.NewBuildingFinance.service.currency;

import com.example.NewBuildingFinance.entities.currency.Currency;
import com.example.NewBuildingFinance.others.CurrencyJson;
import com.example.NewBuildingFinance.repository.CurrencyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService{
    private final CurrencyRepository currencyRepository;

    @Override
    public List<Currency> saveCurrency(List<CurrencyJson> currencyJsons){
        log.info("save currency");
        CurrencyJson eurJson = new CurrencyJson();
        CurrencyJson usdJson = new CurrencyJson();
        for(CurrencyJson currencyJson : currencyJsons) {
            if(currencyJson.getCc().equals("USD")){
                usdJson = currencyJson;
            }
            if(currencyJson.getCc().equals("EUR")){
                eurJson = currencyJson;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        Currency uah = new Currency();
        uah.setName("UAH");
        Double uahPrice = 1 / usdJson.getRate();
        uah.setPrice(Double.valueOf(decimalFormat.format(uahPrice).replace(",", ".")));
        uah.setId(1L);

        Currency eur = new Currency();
        eur.setName(eurJson.getCc());
        Double eurPrice = usdJson.getRate() / eurJson.getRate();
        eur.setPrice(Double.valueOf(decimalFormat.format(eurPrice).replace(",", ".")));
        eur.setId(2L);

        Currency usd = new Currency();
        usd.setName(usdJson.getCc());
        usd.setPrice(1D);
        usd.setId(3L);

        List<Currency> currencies = new ArrayList<>();
        currencies.add(uah);
        currencies.add(eur);
        currencies.add(usd);
        currencyRepository.saveAll(currencies);
        log.info("success save currency");
        return currencies;
    }

    @Override
    public List<Currency> getAll(){
        log.info("get all currency");
        List<Currency> currencies = currencyRepository.findAll();
        log.info("success get all currency");
        return currencies;
    }
}
