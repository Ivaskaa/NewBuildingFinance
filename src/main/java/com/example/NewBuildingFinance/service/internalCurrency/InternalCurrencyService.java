package com.example.NewBuildingFinance.service.internalCurrency;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;

import java.util.List;

public interface InternalCurrencyService {

    /**
     * find all internal currencies
     * @return list of internal currencies
     */
    List<InternalCurrency> findAll();

    /**
     * update internal currency
     * @param objectForm internal currency
     * @return internal currency
     */
    InternalCurrency update(InternalCurrency objectForm);

    /**
     * find internal currency by id
     * @param id id
     * @return internal currency
     */
    InternalCurrency findById(Long id);
}
