package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
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
            advanceStart = Integer.MIN_VALUE;
        }
        if (advanceFin == null){
            advanceFin = Integer.MAX_VALUE;
        }
        Integer finalAdvanceStart = advanceStart;
        Integer finalAdvanceFin = advanceFin;


        return (root, query, cb) -> {
            Join<Flat, FlatPayment> bListJoin = root.join(Flat_.FLAT_PAYMENTS, JoinType.INNER);

//            Subquery<Date> sq = query.subquery(Date.class);
//
//            sq.select(cb.least(bListJoin.<Date>get(FlatPayment_.DATE)));
//
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(cb.equal(bListJoin.get(FlatPayment_.PLANNED), sq));
//            predicates.add(cb.between())
//            cb.least(bListJoin.<String>get(FlatPayment_.DATE));
//            Predicate p = cb.equal(c.get("date"), sq);
//            return cb.between(root.get(FlatPayment_.PLANNED), finalAdvanceStart, finalAdvanceFin);
//            return cb.and(predicates.toArray(new Predicate[0]));
            return null;
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
        Integer finalAdvanceStart = enteredStart;
        Integer finalAdvanceFin = enteredFin;


        return (root, query, cb) -> {

//            Join<Flat, FlatPayment> bListJoin = root.join(Flat_.FLAT_PAYMENTS, JoinType.INNER);
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(cb.like(bListJoin.get(Realtor_.PHONE), "%" + phone.toLowerCase(Locale.ROOT) + "%"));
//            return cb.and(predicates.toArray(new Predicate[0]));
//            return cb.equal(cb.sum(root.get(Flat_.PRICE)));

            return null;
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


        return (root, query, builder) -> {
//            CriteriaBuilder builder = manager.getCriteriaBuilder();
//            CriteriaQuery<OfferEntity> query = builder.createQuery(OfferEntity.class);
//            Root<OfferEntity> root = query.from(OfferEntity.class);

            Join<Flat, FlatPayment> join = root.join(Flat_.FLAT_PAYMENTS, JoinType.INNER);
            query.groupBy(join.get(FlatPayment_.FLAT));

            Root<FlatPayment> rootFlatPayment = query.from(FlatPayment.class);

//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(cb.like(bListJoin.get(Realtor_.PHONE), "%" + phone.toLowerCase(Locale.ROOT) + "%"));
//            return cb.and(predicates.toArray(new Predicate[0]));
//            return cb.equal(cb.sum(root.get(Flat_.PRICE)));

//            return builder.between(
//                    builder.sum(
//                            query.subquery()
//                    ),
//                    finalAdvanceStart, finalAdvanceFin
//            );
            return null;
        };
    }
}
