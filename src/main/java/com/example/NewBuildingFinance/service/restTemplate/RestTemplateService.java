package com.example.NewBuildingFinance.service.restTemplate;

import com.example.NewBuildingFinance.others.CurrencyJson;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface RestTemplateService {

    List<CurrencyJson> getCurrency() throws JsonProcessingException;
}
