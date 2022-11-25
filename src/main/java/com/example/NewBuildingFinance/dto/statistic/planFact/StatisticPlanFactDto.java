package com.example.NewBuildingFinance.dto.statistic.planFact;

import lombok.Data;

import java.util.List;

@Data
public class StatisticPlanFactDto {
    private Integer boxFlats;
    private Integer boxSalesFlats;
    private Integer boxOnSaleFlats;
    private Double boxArea;
    private Double boxSalesArea;
    private Double boxOnSaleArea;

    private Long boxAllFlatSalePrice; // ціни за всі квартири
    private Long boxPlaned; // всі заплановані оплати
    private Long boxFact; // оплачені заплановані оплати
    private Long boxDuty; // залишок ціни за всі квартири - всі заплановані оплати
    private List<StatisticPlanFactTableDto> tableDto;
}
