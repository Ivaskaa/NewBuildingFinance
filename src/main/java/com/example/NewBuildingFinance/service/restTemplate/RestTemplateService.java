package com.example.NewBuildingFinance.service.restTemplate;

import com.example.NewBuildingFinance.others.CurrencyJson;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface RestTemplateService {

    /**
     * request on national Ukrainian bank for currencies
     * @return list of currency json
     * @throws JsonProcessingException
     */
    List<CurrencyJson> getCurrency() throws JsonProcessingException;
}
