package com.example.NewBuildingFinance.service.internalCurrency;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;

import java.util.List;

public interface InternalCurrencyService {

    List<InternalCurrency> findAll();

    InternalCurrency update(InternalCurrency objectForm);

    InternalCurrency findById(Long id);
}
