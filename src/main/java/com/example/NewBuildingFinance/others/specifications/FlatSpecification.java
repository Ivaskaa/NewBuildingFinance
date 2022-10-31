package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.contract.Contract_;
import com.example.NewBuildingFinance.entities.flat.*;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlatSpecification {
    public static Specification<Flat> likeNumber(Integer number) {
        if (number == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Flat_.NUMBER), number);
        };
    }
    public static Specification<Flat> likeObject(Object object) {
        if (object == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Flat_.OBJECT).get(Object_.ID), object.getId());
        };
    }
    public static Specification<Flat> likeStatus(String status) {
        if (status == null || status.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Flat_.STATUS), StatusFlat.valueOf(status));
        };
    }
    public static Specification<Flat> likeArea(Double areaStart, Double areaFin) {
        if (areaStart == null && areaFin == null) {
            return null;
        }
        if (areaStart == null){
            areaStart = Double.MIN_VALUE;
        }
        if (areaFin == null){
            areaFin = Double.MAX_VALUE;
        }
        Double finalAreaStart = areaStart;
        Double finalAreaFin = areaFin;
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.AREA), finalAreaStart, finalAreaFin);
        };
    }
    public static Specification<Flat> likePrice(Integer priceStart, Integer priceFin) {
        if (priceStart == null && priceFin == null) {
            return null;
        }
        if (priceStart == null){
            priceStart = Integer.MIN_VALUE;
        }
        if (priceFin == null){
            priceFin = Integer.MAX_VALUE;
        }
        Integer finalPriceStart = priceStart;
        Integer finalPriceFin = priceFin;
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.PRICE), finalPriceStart, finalPriceFin);
        };
    }
    public static Specification<Flat> likeAdvance(Integer advanceStart, Integer advanceFin) {
        if (advanceStart == null && advanceFin == null) {
            return null;
        }
        if (advanceStart == null){
            advanceStart = Integer.MIN_VALUE;
        }
        if (advanceFin == null){
            advanceFin = Integer.MAX_VALUE;
        }
        Integer finalAdvanceStart = advanceStart;
        Integer finalAdvanceFin = advanceFin;
        return (root, query, cb) -> {
            Join<Flat, FlatPayment> bListJoin = root.join(Flat_.FLAT_PAYMENTS, JoinType.INNER);
//            Join<Flat, FlatPayment> bListJoin = root.join(cb.least(bListJoin.<String>get(FlatPayment_.DATE)), JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();

//            predicates.add(cb.between(cb.least(bListJoin.get(FlatPayment_.DATE)), finalAdvanceStart, finalAdvanceFin));
            cb.least(bListJoin.<String>get(FlatPayment_.DATE));
            return cb.between(cb.least(bListJoin.get(FlatPayment_.DATE)), finalAdvanceStart, finalAdvanceFin);
        };
    }
}
