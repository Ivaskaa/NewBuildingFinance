package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.contract.Contract_;
import com.example.NewBuildingFinance.entities.flat.*;
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
    public static Specification<Flat> likeObjectId(Long objectId) {
        if (objectId == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Flat_.OBJECT).get(Object_.ID), objectId);
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
            advanceStart = 0;
        }
        if (advanceFin == null){
            advanceFin = Integer.MAX_VALUE;
        }
        Integer finalAdvanceStart = advanceStart;
        Integer finalAdvanceFin = advanceFin;


        return (root, query, cb) -> {
            Subquery<Long> subFlatPaymentForDate = query.subquery(Long.class);
            Root<FlatPayment> subRootForDate = subFlatPaymentForDate.from(FlatPayment.class);

            subFlatPaymentForDate.select(cb.min(subRootForDate.get(FlatPayment_.DATE)));
            subFlatPaymentForDate.where(cb.equal(subRootForDate.get(FlatPayment_.FLAT).get(Flat_.ID), root.get(Flat_.ID)));
            subFlatPaymentForDate.groupBy(subRootForDate.get(FlatPayment_.FLAT).get(Flat_.ID));

            SetJoin<Flat, FlatPayment> subJoin = root.joinSet(Flat_.FLAT_PAYMENTS, JoinType.INNER);

            query.groupBy(subJoin.get(FlatPayment_.FLAT).get(Flat_.ID));

            return cb.and(
                    cb.between(subJoin.get(FlatPayment_.PLANNED), finalAdvanceStart, finalAdvanceFin),
                    cb.equal(subJoin.get(FlatPayment_.DATE), subFlatPaymentForDate)
            );
        };
    }

    public static Specification<Flat> likeEntered(Integer enteredStart, Integer enteredFin) {
        if (enteredStart == null && enteredFin == null) {
            return null;
        }
        if (enteredStart == null){
            enteredStart = Integer.MIN_VALUE;
        }
        if (enteredFin == null){
            enteredFin = Integer.MAX_VALUE;
        }
        Integer finalEnteredStart = enteredStart;
        Integer finalEnteredFin = enteredFin;


        return (root, query, cb) -> {
            Subquery<Integer> subFlatPayment = query.subquery(Integer.class);
            Root<FlatPayment> subRoot = subFlatPayment.from(FlatPayment.class);

            subFlatPayment.select(cb.sum(subRoot.get(FlatPayment_.ACTUALLY)));
            subFlatPayment.where(cb.equal(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID), root.get(Flat_.ID)));
            subFlatPayment.groupBy(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID));

            return cb.between(subFlatPayment, finalEnteredStart, finalEnteredFin);
        };
    }

    public static Specification<Flat> likeRemains(Integer remainsStart, Integer remainsFin) {
        if (remainsStart == null && remainsFin == null) {
            return null;
        }
        if (remainsStart == null){
            remainsStart = Integer.MIN_VALUE;
        }
        if (remainsFin == null){
            remainsFin = Integer.MAX_VALUE;
        }
        Integer finalAdvanceStart = remainsStart;
        Integer finalAdvanceFin = remainsFin;


        return (root, query, cb) -> {
            Subquery<Integer> subFlatPayment = query.subquery(Integer.class);
            Root<FlatPayment> subRoot = subFlatPayment.from(FlatPayment.class);

            subFlatPayment.select(
                    cb.sum(
                            cb.diff(
                                    subRoot.get(FlatPayment_.PLANNED),
                                    cb.coalesce(subRoot.get(FlatPayment_.ACTUALLY), 0))));
            subFlatPayment.where(cb.and(
                    cb.equal(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID), root.get(Flat_.ID)),
                    cb.isFalse(subRoot.get(FlatPayment_.DELETED))
            ));
            subFlatPayment.groupBy(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID));

            return cb.between(subFlatPayment, finalAdvanceStart, finalAdvanceFin);
        };
    }

    public static Specification<Flat> deletedFalse() {
        return (root, query, cb) -> {
            return cb.isFalse(root.get(Flat_.DELETED));
        };
    }
}
