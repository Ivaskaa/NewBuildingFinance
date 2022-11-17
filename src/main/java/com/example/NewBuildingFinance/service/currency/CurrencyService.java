package com.example.NewBuildingFinance.service.currency;

import com.example.NewBuildingFinance.entities.currency.Currency;
import com.example.NewBuildingFinance.others.CurrencyJson;

import java.util.List;

public interface CurrencyService {

    void saveCurrency(List<CurrencyJson> currencyJsons);

    List<Currency> getAll();
}
