package com.example.NewBuildingFinance.dto.statistic;

import lombok.Data;

import java.util.Date;

@Data
public class PlanFactStatisticTableDto {
    private Date date;
    private Double planed;
    private Double fact;
    private Double duty;
}
