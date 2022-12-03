package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.cashRegister.*;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency_;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CashRegisterSpecification {
    public static Specification<CashRegister> likeNumber(Long number) {
        if (number == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.NUMBER), number);
        };
    }
    public static Specification<CashRegister> likeDate(String dateStartString, String dateFinString) throws ParseException {
        if (dateStartString.equals("") && dateFinString.equals("")) {
            return null;
        }
        Date dateStart = new Date(1L);
        Date dateFin = new Date(Long.MAX_VALUE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (!dateStartString.equals("")){
            dateStart = format.parse(dateStartString);
        }
        if (!dateFinString.equals("")){
            dateFin = format.parse(dateFinString);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFin);
        calendar.add(Calendar.DATE, 1);

        Date finalDateStart = dateStart;
        Date finalDateFin = calendar.getTime();
        return (root, query, cb) -> {
            return cb.between(root.get(CashRegister_.DATE), finalDateStart, finalDateFin);
        };
    }
    public static Specification<CashRegister> likeEconomic(String economic) {
        if (economic == null || economic.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.ECONOMIC), Economic.valueOf(economic));
        };
    }
    public static Specification<CashRegister> likeStatus(String status) {
        if (status == null || status.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.STATUS), StatusCashRegister.valueOf(status));
        };
    }
    public static Specification<CashRegister> likeObjectId(Long objectId) {
        if (objectId == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.OBJECT).get(Object_.ID), objectId);
        };
    }
    public static Specification<CashRegister> likeArticle(String article) {
        if (article == null || article.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.ARTICLE), Article.valueOf(article));
        };
    }
    public static Specification<CashRegister> likePrice(Double priceStart, Double priceFin) {
        if (priceStart == null && priceFin == null) {
            return null;
        }
        if (priceStart == null){
            priceStart = Double.MIN_VALUE;
        }
        if (priceFin == null){
            priceFin = Double.MAX_VALUE;
        }
        Double finalAdvanceStart = priceStart;
        Double finalAdvanceFin = priceFin;
        return (root, query, cb) -> {
            return cb.between(root.get(CashRegister_.PRICE), finalAdvanceStart, finalAdvanceFin);
        };
    }
    public static Specification<CashRegister> likeCurrencyId(Long currencyId) {
        if (currencyId == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(CashRegister_.CURRENCY).get(InternalCurrency_.ID), currencyId);
        };
    }
    public static Specification<CashRegister> likeCounterparty(String counterparty) {
        if (counterparty == null || counterparty.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.like(root.get(CashRegister_.COUNTERPARTY), "%" + counterparty.toLowerCase(Locale.ROOT) + "%");
        };
    }
}
