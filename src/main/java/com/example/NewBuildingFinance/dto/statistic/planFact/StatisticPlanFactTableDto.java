package com.example.NewBuildingFinance.dto.statistic.planFact;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticPlanFactTableDto {
    private Date date;
    private Double planed;
    private Double fact;
    private Double duty;
}
