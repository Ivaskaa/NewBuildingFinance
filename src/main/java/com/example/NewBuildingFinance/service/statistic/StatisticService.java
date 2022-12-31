package com.example.NewBuildingFinance.service.statistic;

import com.example.NewBuildingFinance.dto.statistic.StatisticCashRegisterDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatPaymentBarChartDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsSearchDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsTableDto;
import com.example.NewBuildingFinance.dto.statistic.planFact.StatisticPlanFactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface StatisticService {

    /**
     * general monthly statistic
     * @param objectId object id (if null return about all objects)
     * @param dateStartString date start
     * @param dateFinString date fin
     * @return count flat (sales flat, on sale flat)
     * flats area (sales flats area, on sale flats area)
     * all flat price (planed, fact, remains)
     * list of month with sum(planed, fact, remains) in this month
     * @throws ParseException
     */
    StatisticPlanFactDto getMainMonthlyStatistic(
            Long objectId,
            String dateStartString,
            String dateFinString
    ) throws ParseException;

    /**
     * flats statistic
     * @param searchDto search dto with pageable variables and variables for filtering
     * @return page of flats
     */
    Page<StatisticFlatsTableDto> getFlatPaymentStatistic(StatisticFlatsSearchDto searchDto);

    /**
     * get min date and max date
     * @return min date and max day from flat payments
     */
    Pair<Date, Date> getMinDateAndMaxDate();

    /**
     * cash register statistic boxes
     * @return fact (UAH, USD, EUR)
     * income (UAH, USD, EUR)
     * spending (UAH, USD, EUR)
     */
    StatisticCashRegisterDto getCashRegisterBoxes();

    /**
     * flat statistic boxes
     * @return count flat (sales flat, on sale flat)
     * flats area (sales flats area, on sale flats area)
     * planed, fact, remains flat price
     */
    StatisticFlatsDto getFlatBoxes();

    /**
     * get flat payments by flat id
     * @param flatId flat id
     * @return flat payments info for statistic
     */
    List<StatisticFlatPaymentBarChartDto> getFlatPaymentsByFlatId(Long flatId);
}
