package com.example.NewBuildingFinance.dto.statistic.flats;

import com.example.NewBuildingFinance.dto.SearchDto;
import lombok.Data;

@Data
public class StatisticFlatsSearchDto extends SearchDto {
    private Integer flatNumber;
    private Long objectId;
    private Double priceStart;
    private Double priceFin;
    private Double salePriceStart;
    private Double salePriceFin;
    private Double factStart;
    private Double factFin;
    private Double remainsStart;
    private Double remainsFin;
    private Double debtStart;
    private Double debtFin;
    private String status;
    private Long contractId;
    private String buyer;
    private String realtor;
    private Double sale;
}
