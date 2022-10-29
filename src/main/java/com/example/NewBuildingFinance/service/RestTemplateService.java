package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.others.CurrencyJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class RestTemplateService {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    public List<CurrencyJson> getCurrency() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<CurrencyJson> currencyBanks =
                mapper.readValue(restTemplate.exchange(
                                "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json",
                                HttpMethod.GET,
                                entity,
                                String.class).getBody(),
                        mapper.getTypeFactory().constructCollectionType(List.class, CurrencyJson.class));
        currencyBanks = currencyBanks.stream()
                .filter(object -> object.getTxt().equals("Долар США") || object.getTxt().equals("Євро"))
                .collect(Collectors.toList());
        for (CurrencyJson currencyJson : currencyBanks){
            currencyJson.setRate(Double.parseDouble(String.format("%.2f", currencyJson.getRate()).replace(',', '.')));
        }
        return currencyBanks;
    }
}
