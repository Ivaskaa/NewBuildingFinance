package com.example.NewBuildingFinance.service.restTemplate;

import com.example.NewBuildingFinance.others.CurrencyJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService{
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Override
    public List<CurrencyJson> getCurrency() throws JsonProcessingException {
        log.info("get currency from national bank api");

        String response = restTemplate.getForObject(
                "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json",
                String.class);

        System.out.println(response);

        List<CurrencyJson> currencyBanks =
                mapper.readValue(response, new TypeReference<>(){});

        currencyBanks = currencyBanks.stream()
                .filter(object -> object.getTxt().equals("Долар США") || object.getTxt().equals("Євро"))
                .collect(Collectors.toList());
        for (CurrencyJson currencyJson : currencyBanks){
            currencyJson.setRate(Double.parseDouble(String.format("%.2f", currencyJson.getRate()).replace(',', '.')));
        }
        log.info("success get currency from national bank api");
        return currencyBanks;
    }
}
