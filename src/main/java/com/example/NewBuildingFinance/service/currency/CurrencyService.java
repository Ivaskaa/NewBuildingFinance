package com.example.NewBuildingFinance.service.currency;

import com.example.NewBuildingFinance.entities.currency.Currency;
import com.example.NewBuildingFinance.others.CurrencyJson;

import java.util.List;

public interface CurrencyService {

    /**
     * save currency list from national bank api
     * @param currencyJsons currency list
     */
    List<Currency> saveCurrency(List<CurrencyJson> currencyJsons);

    /**
     * get all currencies
     * @return currency list
     */
    List<Currency> getAll();
}
