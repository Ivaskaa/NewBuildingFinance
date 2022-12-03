package com.example.NewBuildingFinance.dto.statistic.flats;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticFlatPaymentBarChartDto {
    private Date date;
    private Double planed;
    private Double fact;
    private Double remains;
}
