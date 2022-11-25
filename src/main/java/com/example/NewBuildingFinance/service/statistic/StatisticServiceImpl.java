package com.example.NewBuildingFinance.service.statistic;

import com.example.NewBuildingFinance.dto.statistic.CashRegisterDto;
import com.example.NewBuildingFinance.dto.statistic.PlanFactStatisticDto;
import com.example.NewBuildingFinance.dto.statistic.PlanFactStatisticTableDto;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.repository.InternalCurrencyRepository;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Service
@Log4j2
@AllArgsConstructor
public class StatisticServiceImpl {
    private final FlatRepository flatRepository;
    private final FlatPaymentRepository flatPaymentRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final InternalCurrencyRepository internalCurrencyRepository;
    private final InternalCurrencyServiceImpl internalCurrencyService;

    public PlanFactStatisticDto getMainMonthlyStatistic(Long objectId, String dateStartString, String dateFinString) throws ParseException {
        List<Flat> flats;
        if (objectId != null){
            flats = flatRepository.findAllByObjectId(objectId);
        } else {
            flats = flatRepository.findAll();
        }

        Integer boxFlats = flats.size();
        int boxSalesFlats = 0;
        int boxOnSaleFlats = 0;
        Double boxArea = 0d;
        Double boxSalesArea = 0d;
        Double boxOnSaleArea = 0d;
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
        List<PlanFactStatisticTableDto> list = new ArrayList<>();

        for(; localDateStart.isBefore(localDateFin); localDateStart = localDateStart.plusMonths(1)){
            boxSalesFlats = 0;
            boxOnSaleFlats = 0;
            boxArea = 0d;
            boxSalesArea = 0d;
            boxOnSaleArea = 0d;

            PlanFactStatisticTableDto dto = new PlanFactStatisticTableDto();
            dto.setDate(Date.from(localDateStart.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Double planed = 0d;
            Double fact = 0d;


            for(Flat flat : flats){
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

        PlanFactStatisticDto planFactStatisticDto = new PlanFactStatisticDto();
        planFactStatisticDto.setTableDto(list);

        planFactStatisticDto.setBoxArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxArea)
                        .replace(",", ".")));
        planFactStatisticDto.setBoxSalesArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxSalesArea)
                        .replace(",", ".")));
        planFactStatisticDto.setBoxOnSaleArea(
                Double.valueOf(new DecimalFormat("#0.00").format(boxOnSaleArea)
                        .replace(",", ".")));

        planFactStatisticDto.setBoxPlaned(Double.valueOf(boxPlaned).longValue());
        planFactStatisticDto.setBoxFact(Double.valueOf(boxFact).longValue());
        planFactStatisticDto.setBoxDuty(Double.valueOf(boxDuty).longValue());

        planFactStatisticDto.setBoxFlats(boxFlats);
        planFactStatisticDto.setBoxSalesFlats(boxSalesFlats);
        planFactStatisticDto.setBoxOnSaleFlats(boxOnSaleFlats);
        return planFactStatisticDto;
    }

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


    public CashRegisterDto getCashRegisterBoxes() {
        CashRegisterDto cashRegisterDto = new CashRegisterDto();

        long factUah;
        long factUsd = 0L;
        long factEur = 0L;
        long incomeUah = 0L;
        long incomeUsd = 0L;
        long incomeEur = 0L;
        long spendingUah = 0L;
        long spendingUsd = 0L;
        long spendingEur = 0L;

        List<CashRegister> cashRegisters = cashRegisterRepository.findAll();
        for(CashRegister cashRegister : cashRegisters){
            if(cashRegister.getStatus().equals(StatusCashRegister.COMPLETED)) {
                if (cashRegister.getEconomic().equals(Economic.INCOME)) {
                    double income = cashRegister.getPrice() * cashRegister.getAdmissionCourse();
                    incomeUah += Math.round(income);
                } else {
                    double spending = cashRegister.getPrice() * cashRegister.getAdmissionCourse();
                    spendingUah += Math.round(spending);
                }
            }
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

        cashRegisterDto.setFactUah(factUah);
        cashRegisterDto.setFactUsd(factUsd);
        cashRegisterDto.setFactEur(factEur);
        cashRegisterDto.setIncomeUah(incomeUah);
        cashRegisterDto.setIncomeUsd(incomeUsd);
        cashRegisterDto.setIncomeEur(incomeEur);
        cashRegisterDto.setSpendingUah(spendingUah);
        cashRegisterDto.setSpendingUsd(spendingUsd);
        cashRegisterDto.setSpendingEur(spendingEur);

        return cashRegisterDto;
    }
}
