package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.cashRegister.*;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency_;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.FlatPayment_;
import com.example.NewBuildingFinance.entities.flat.Flat_;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.cache.Cache;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        String[] words = counterparty.split(" ");

        return (root, query, cb) -> {
//            Subquery<String> subBuyers = query.subquery(String.class);
//            Root<Buyer> rootBuyers = subBuyers.from(Buyer.class);
//
//            Subquery<String> subFlats = query.subquery(String.class);
//            Root<Flat> rootFlats = subFlats.from(Flat.class);
//
//            Subquery<String> subFlatPayments = query.subquery(String.class);
//            Root<FlatPayment> rootFlatPayments = subFlatPayments.from(FlatPayment.class);
//
//            subBuyers.select(rootBuyers.get(Buyer_.NAME));
//            subBuyers.where(cb.equal(rootBuyers.get(Buyer_.ID).get(FlatPayment_.ID), rootFlats.get(Flat_.BUYER).get(Buyer_.ID)));
//
//            subFlats.select(subBuyers);
//            subFlats.where(cb.equal(rootFlats.get(Flat_.ID), rootFlatPayments.get(FlatPayment_.ID).get(Flat_.ID)));
//
//            subFlatPayments.select(subFlats);
//            subFlatPayments.where(cb.equal(rootFlatPayments.get(FlatPayment_.ID), root.get(CashRegister_.FLAT_PAYMENT).get(FlatPayment_.ID)));
//
//            CriteriaBuilder
//
//            subFlatPayments.select();
//            subFlatPayments.where(cb.equal(root.get(CashRegister_.FLAT_PAYMENT).get(FlatPayment_.ID), rootFlatPayments.get(FlatPayment_.ID)));
//            subFlatPayments.groupBy(rootFlatPayments.get(FlatPayment_.FLAT).get(Flat_.ID));

//
//
//            Join<Object, Object> bListJoin = root.join(Agency_.REALTORS, JoinType.INNER);
//            if(words.length == 1){
//                return cb.or(
//                        cb.and(
//                                cb.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
//                                cb.isTrue(bListJoin.get(Realtor_.DIRECTOR))),
//                        cb.and(
//                                cb.like(bListJoin.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
//                                cb.isTrue(bListJoin.get(Realtor_.DIRECTOR)))
//                );
//            } else if(words.length == 2){
//                return cb.or(
//                        cb.and(
//                                cb.like(bListJoin.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
//                                cb.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
//                                cb.isTrue(bListJoin.get(Realtor_.DIRECTOR))),
//                        cb.and(
//                                cb.like(bListJoin.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
//                                cb.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
//                                cb.isTrue(bListJoin.get(Realtor_.DIRECTOR)))
//                );
//            } else {
//                return cb.equal(root.get(CashRegister_.ID), 0);
//            }
            return null;
        };
    }

    public static Specification<CashRegister> deletedFalse() {
        return (root, query, cb) -> {
            return cb.isFalse(root.get(CashRegister_.DELETED));
        };
    }
}
