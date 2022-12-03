package com.example.NewBuildingFinance.dto.statistic.flats;

import lombok.Data;

@Data
public class StatisticFlatsTableDto {
    private Long flatId;
    private Integer flatNumber;
    private String object;
    private Double price;
    private Double salePrice;
    private Double fact;
    private Double remains;
    private Double debt;
    private String status;
    private Long contractId;
    private String buyer;
    private String realtor;
    private Double sale;
}
