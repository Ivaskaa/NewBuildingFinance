package com.example.NewBuildingFinance.dto.statistic;

import lombok.Data;

@Data
public class StatisticCashRegisterDto {
    private Long factUah;
    private Long factUsd;
    private Long factEur;
    private Long incomeUah;
    private Long incomeUsd;
    private Long incomeEur;
    private Long spendingUah;
    private Long spendingUsd;
    private Long spendingEur;
}
