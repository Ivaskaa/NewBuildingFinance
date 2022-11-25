package com.example.NewBuildingFinance.dto.statistic.flatPayments;

import lombok.Data;

@Data
public class StatisticFlatsDto {
    private Long boxCountFlats;
    private Long boxCountFlatsSales;
    private Long boxCountFlatsOnSale;

    private Long boxAllFlatSalePrice; // ціни за всі квартири
    private Long boxPlaned; // всі заплановані оплати
    private Long boxFact; // оплачені заплановані оплати
    private Long boxDuty; // залишок ціни за всі квартири - всі заплановані оплати

    private Long boxArrears; // запланована оплата - фактична оплата
}
