package com.example.NewBuildingFinance.dto.statistic;

import lombok.Data;

import java.util.List;

@Data
public class PlanFactStatisticDto {
    private Integer boxFlats;
    private Integer boxSalesFlats;
    private Integer boxOnSaleFlats;
    private Double boxArea;
    private Double boxSalesArea;
    private Double boxOnSaleArea;
    private Long boxPlaned;
    private Long boxFact;
    private Long boxDuty;
    private List<PlanFactStatisticTableDto> tableDto;
}
