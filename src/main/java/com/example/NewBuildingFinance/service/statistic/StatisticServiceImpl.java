package com.example.NewBuildingFinance.service.statistic;

import com.example.NewBuildingFinance.dto.statistic.StatisticCashRegisterDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatPaymentBarChartDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsSearchDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsTableDto;
import com.example.NewBuildingFinance.dto.statistic.planFact.StatisticPlanFactDto;
import com.example.NewBuildingFinance.dto.statistic.planFact.StatisticPlanFactTableDto;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.others.specifications.FlatStatisticSpecification;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyService;
import com.example.NewBuildingFinance.service.staticService.StaticService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class StatisticServiceImpl implements StatisticService{
    private final FlatRepository flatRepository;
    private final FlatPaymentRepository flatPaymentRepository;
    private final CashRegisterRepository cashRegisterRepository;

    private final InternalCurrencyService internalCurrencyService;
    private final StaticService staticService;

    @Override
    public StatisticPlanFactDto getMainMonthlyStatistic(
            Long objectId,
            String dateStartString,
            String dateFinString
    ) throws ParseException {
        List<Flat> flats;
        if (objectId != null){
            flats = flatRepository.findAllByObjectIdAndDeletedFalse(objectId);
        } else {
            flats = flatRepository.findAllByDeletedFalse();
        }

        Integer boxFlats = flats.size();
        int boxSalesFlats = 0;
        int boxOnSaleFlats = 0;
        Double boxArea = 0d;
        Double boxSalesArea = 0d;
        Double boxOnSaleArea = 0d;
        double boxAllFlatSalePrice = 0d;
        double boxPlaned = 0d;
        double boxFact = 0d;
        double boxDuty = 0d;

        Pair<Date, Date> datePair = null;
        Date dateStart;
        Date dateFin;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        if(dateStartString.equals("") || dateFinString.equals("")){
            datePair = getMinDateAndMaxDate();
        }
        if(dateStartString.equals("")){
            dateStart = datePair.getFirst();
        } else {
            dateStart = format.parse(dateStartString);
        }
        if(dateFinString.equals("")){
            dateFin = datePair.getSecond();
        } else {
            dateFin = format.parse(dateFinString);
        }

        LocalDate localDateStart = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateFin = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDateFin = localDateFin.plusMonths(1);
        List<StatisticPlanFactTableDto> list = new ArrayList<>();

        for(; localDateStart.isBefore(localDateFin); localDateStart = localDateStart.plusMonths(1)){
            boxAllFlatSalePrice = 0;
            boxSalesFlats = 0;
            boxOnSaleFlats = 0;
            boxArea = 0d;
            boxSalesArea = 0d;
            boxOnSaleArea = 0d;

            StatisticPlanFactTableDto dto = new StatisticPlanFactTableDto();
            dto.setDate(Date.from(localDateStart.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Double planed = 0d;
            Double fact = 0d;


            for(Flat flat : flats){
                boxAllFlatSalePrice += flat.getSalePrice();
                boxArea += flat.getArea();
                if(flat.getStatus().equals(StatusFlat.SOLD)){
                    boxSalesFlats += 1;
                    boxSalesArea += flat.getArea();
                } else {
                    boxOnSaleFlats += 1;
                    boxOnSaleArea += flat.getArea();
                }
                if(!flat.getFlatPayments().isEmpty()){
                    for(FlatPayment flatPayment : flat.getFlatPayments()){
                        Calendar flatPaymentCalendar = Calendar.getInstance();
                        Calendar monthCalendar = Calendar.getInstance();
                        flatPaymentCalendar.setTime(flatPayment.getDate());
                        monthCalendar.setTime(Date.from(localDateStart.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        if(flatPaymentCalendar.get(Calendar.YEAR) == monthCalendar.get(Calendar.YEAR)) {
                            if (flatPaymentCalendar.get(Calendar.MONTH) == monthCalendar.get(Calendar.MONTH)) {
                                planed += flatPayment.getPlanned();
                                if (flatPayment.getActually() != null) {
                                    fact += flatPayment.getActually();
                                }
                            }
                        }
                    }
                }
            }
            boxPlaned += planed;
            boxFact += fact;
            boxDuty += planed - fact;
            dto.setPlaned(planed);
            dto.setFact(fact);
            dto.setDuty(planed - fact);
            list.add(dto);
        }

        StatisticPlanFactDto statisticPlanFactDto = new StatisticPlanFactDto();
        statisticPlanFactDto.setTableDto(list);

        statisticPlanFactDto.setBoxArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxArea)
                        .replace(",", ".")));
        statisticPlanFactDto.setBoxSalesArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxSalesArea)
                        .replace(",", ".")));
        statisticPlanFactDto.setBoxOnSaleArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxOnSaleArea)
                        .replace(",", ".")));


        statisticPlanFactDto.setBoxAllFlatSalePrice(Math.round(boxAllFlatSalePrice));
        statisticPlanFactDto.setBoxPlaned(Math.round(boxPlaned));
        statisticPlanFactDto.setBoxFact(Math.round(boxFact));
        statisticPlanFactDto.setBoxDuty(Math.round(boxDuty));

        statisticPlanFactDto.setBoxFlats(boxFlats);
        statisticPlanFactDto.setBoxSalesFlats(boxSalesFlats);
        statisticPlanFactDto.setBoxOnSaleFlats(boxOnSaleFlats);
        return statisticPlanFactDto;
    }
    @Override
    public Page<StatisticFlatsTableDto> getFlatPaymentStatistic(StatisticFlatsSearchDto searchDto){
        log.info("get statistic page. page: {}, size: {} field: {}, direction: {}",
                searchDto.getPage() - 1, searchDto.getSize(),
                searchDto.getSortingField(), searchDto.getSortingDirection());
        Specification<Flat> specification = Specification
                .where(FlatStatisticSpecification.likeNumber(searchDto.getFlatNumber()))
                .and(FlatStatisticSpecification.likeObjectId(searchDto.getObjectId()))
                .and(FlatStatisticSpecification.likePrice(searchDto.getPriceStart(), searchDto.getPriceFin()))
                .and(FlatStatisticSpecification.likeSalePrice(searchDto.getSalePriceStart(), searchDto.getSalePriceFin()))
                .and(FlatStatisticSpecification.betweenFact(searchDto.getFactStart(), searchDto.getFactFin()))
                .and(FlatStatisticSpecification.betweenRemains(searchDto.getRemainsStart(), searchDto.getRemainsFin()))
                .and(FlatStatisticSpecification.betweenDebt(searchDto.getDebtStart(), searchDto.getDebtFin()))
                .and(FlatStatisticSpecification.likeStatus(searchDto.getStatus()))
                .and(FlatStatisticSpecification.likeContractId(searchDto.getContractId()))
                .and(FlatStatisticSpecification.likeBuyer(searchDto.getBuyer()))
                .and(FlatStatisticSpecification.likeRealtor(searchDto.getRealtor()))
                .and(FlatStatisticSpecification.likeSale(searchDto.getSale()))
                .and(FlatStatisticSpecification.deletedFalse());

        Sort sort = staticService.sort(searchDto.getSortingField(), searchDto.getSortingDirection());
        Pageable pageable = PageRequest.of(searchDto.getPage() - 1, searchDto.getSize(), sort);
        Page<StatisticFlatsTableDto> statisticFlatPayments = flatRepository.findAll(specification, pageable).map(Flat::buildStatisticTableDto);
        log.info("success get statistic page.");
        return statisticFlatPayments;
    }
    @Override
    public Pair<Date, Date> getMinDateAndMaxDate() {
        List<FlatPayment> flatPayments = flatPaymentRepository.findAll();
        Date first = new Date(99999999999999L);
        Date second = new Date(Long.MIN_VALUE);

        for(FlatPayment flatPayment : flatPayments){
            if(first.after(flatPayment.getDate())){
                first = flatPayment.getDate();
            }
            if(second.before(flatPayment.getDate())){
                second = flatPayment.getDate();
            }
        }

        if(first.equals(new Date(99999999999999L))){
            first = new Date();
        }
        if(second.equals(new Date(Long.MIN_VALUE))){
            second = new Date();
        }
        return Pair.of(first, second);
    }

    @Override
    public StatisticCashRegisterDto getCashRegisterBoxes() {
        StatisticCashRegisterDto statisticCashRegisterDto = new StatisticCashRegisterDto();

        long factUah;
        long factUsd = 0L;
        long factEur = 0L;
        long incomeUah = 0L;
        long incomeUsd = 0L;
        long incomeEur = 0L;
        long spendingUah = 0L;
        long spendingUsd = 0L;
        long spendingEur = 0L;

        try {
            List<CashRegister> cashRegisters = cashRegisterRepository.findAll();
            for (CashRegister cashRegister : cashRegisters) {
                if (cashRegister.getStatus().equals(StatusCashRegister.COMPLETED)) {
                    if (cashRegister.getEconomic().equals(Economic.INCOME)) {
                        double income = cashRegister.getPrice() * cashRegister.getAdmissionCourse();
                        incomeUah += Math.round(income);
                    } else {
                        double spending = cashRegister.getPrice() * cashRegister.getAdmissionCourse();
                        spendingUah += Math.round(spending);
                    }
                }
            }
        } catch (Exception ignored){

        }

        factUah = incomeUah - spendingUah;
        List<InternalCurrency> internalCurrencies = internalCurrencyService.findAll();

        for(InternalCurrency internalCurrency :internalCurrencies){
            if(internalCurrency.getName().equals("USD")){
                double income = incomeUah / internalCurrency.getPrice();
                incomeUsd = Math.round(income);
                double spending = spendingUah / internalCurrency.getPrice();
                spendingUsd = Math.round(spending);
                double fact = factUah / internalCurrency.getPrice();
                factUsd = Math.round(fact);
            }
            if(internalCurrency.getName().equals("EUR")){
                double income = incomeUah / internalCurrency.getPrice();
                incomeEur = Math.round(income);
                double spending = spendingUah / internalCurrency.getPrice();
                spendingEur = Math.round(spending);
                double fact = factUah / internalCurrency.getPrice();
                factEur = Math.round(fact);
            }
        }

        statisticCashRegisterDto.setFactUah(factUah);
        statisticCashRegisterDto.setFactUsd(factUsd);
        statisticCashRegisterDto.setFactEur(factEur);
        statisticCashRegisterDto.setIncomeUah(incomeUah);
        statisticCashRegisterDto.setIncomeUsd(incomeUsd);
        statisticCashRegisterDto.setIncomeEur(incomeEur);
        statisticCashRegisterDto.setSpendingUah(spendingUah);
        statisticCashRegisterDto.setSpendingUsd(spendingUsd);
        statisticCashRegisterDto.setSpendingEur(spendingEur);

        return statisticCashRegisterDto;
    }
    @Override
    public StatisticFlatsDto getFlatBoxes() {
        StatisticFlatsDto statistics = new StatisticFlatsDto();
        List<Flat> flats = flatRepository.findAllByDeletedFalse();

        long boxCountFlats;
        long boxCountFlatsSales = 0L;
        long boxCountFlatsOnSale = 0L;

        long boxAllFlatSalePrice = 0L;
        long boxPlaned = 0L;
        long boxFact = 0L;
        long boxDuty;

        long boxArrears = 0L;

        boxCountFlats = flats.size();

        for(Flat flat : flats){
            if(flat.getStatus().equals(StatusFlat.SOLD)){
                boxCountFlatsSales++;
            } else {
                boxCountFlatsOnSale++;
            }
            boxAllFlatSalePrice += flat.getSalePrice();
            for(FlatPayment flatPayment : flat.getFlatPayments()){
                if(flatPayment.isPaid()){
                    boxArrears += Math.round(flatPayment.getPlanned() - flatPayment.getActually());
                    boxFact += flatPayment.getActually();
                }
                boxPlaned += flatPayment.getPlanned();
            }
        }
        boxDuty = boxAllFlatSalePrice - boxPlaned;

        statistics.setBoxCountFlats(boxCountFlats);
        statistics.setBoxCountFlatsSales(boxCountFlatsSales);
        statistics.setBoxCountFlatsOnSale(boxCountFlatsOnSale);
        statistics.setBoxAllFlatSalePrice(boxAllFlatSalePrice);
        statistics.setBoxPlaned(boxPlaned);
        statistics.setBoxFact(boxFact);
        statistics.setBoxDuty(boxDuty);
        statistics.setBoxArrears(boxArrears);
        return statistics;
    }
    @Override
    public List<StatisticFlatPaymentBarChartDto> getFlatPaymentsByFlatId(Long flatId) {
        Sort sort = Sort.by(Sort.Direction.valueOf("ASC"), "date");
        List<StatisticFlatPaymentBarChartDto> statistic =
                flatPaymentRepository.findAllByFlatIdAndDeletedFalse(sort, flatId)
                .stream()
                .map(FlatPayment::buildFlatPaymentForBarChart)
                .collect(Collectors.toList());
        return statistic;
    }
}
