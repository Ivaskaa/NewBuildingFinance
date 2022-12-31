package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.contract.Contract_;
import com.example.NewBuildingFinance.entities.flat.*;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlatStatisticSpecification {
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
    public static Specification<Flat> likePrice(Double priceStart, Double priceFin) {
        if (priceStart == null && priceFin == null) {
            return null;
        }
        if (priceStart == null){
            priceStart = Double.MIN_VALUE;
        }
        if (priceFin == null){
            priceFin = Double.MAX_VALUE;
        }
        Double finalPriceStart = priceStart;
        Double finalPriceFin = priceFin;
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.PRICE), finalPriceStart, finalPriceFin);
        };
    }
    public static Specification<Flat> likeSalePrice(Double priceStart, Double priceFin) {
        if (priceStart == null && priceFin == null) {
            return null;
        }
        if (priceStart == null){
            priceStart = Double.MIN_VALUE;
        }
        if (priceFin == null){
            priceFin = Double.MAX_VALUE;
        }
        Double finalPriceStart = priceStart;
        Double finalPriceFin = priceFin;
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.SALE_PRICE), finalPriceStart, finalPriceFin);
        };
    }
    public static Specification<Flat> betweenFact(Double factStart, Double factFin) {
        if (factStart == null && factFin == null) {
            return null;
        }
        if (factStart == null){
            factStart = Double.MIN_VALUE;
        }
        if (factFin == null){
            factFin = Double.MAX_VALUE;
        }
        Double finalFactStart = factStart;
        Double finalFactFin = factFin;


        return (root, query, cb) -> {
            Subquery<Double> subFlatPayment = query.subquery(Double.class);
            Root<FlatPayment> subRoot = subFlatPayment.from(FlatPayment.class);

            subFlatPayment.select(cb.sum(subRoot.get(FlatPayment_.ACTUALLY)));
            subFlatPayment.where(cb.equal(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID), root.get(Flat_.ID)));
            subFlatPayment.groupBy(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID));

            return cb.between(subFlatPayment, finalFactStart, finalFactFin);
        };
    }
    public static Specification<Flat> betweenRemains(Double remainsStart, Double remainsFin) {
        if (remainsStart == null && remainsFin == null) {
            return null;
        }
        if (remainsStart == null){
            remainsStart = Double.MIN_VALUE;
        }
        if (remainsFin == null){
            remainsFin = Double.MAX_VALUE;
        }
        Integer finalAdvanceStart = remainsStart.intValue();
        Integer finalAdvanceFin = remainsFin.intValue();


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
    public static Specification<Flat> betweenDebt(Double debtStart, Double debtFin) {
        if (debtStart == null && debtFin == null) {
            return null;
        }
        if (debtStart == null){
            debtStart = Double.MIN_VALUE;
        }
        if (debtFin == null){
            debtFin = Double.MAX_VALUE;
        }
        Double finalDebtStart = debtStart;
        Double finalDebtFin = debtFin;


        return (root, query, cb) -> {
            Subquery<Double> subFlatPayment = query.subquery(Double.class);
            Root<FlatPayment> subRoot = subFlatPayment.from(FlatPayment.class);

            subFlatPayment.select(
                    cb.sum(
                            cb.diff(
                                    subRoot.get(FlatPayment_.PLANNED),
                                    subRoot.get(FlatPayment_.ACTUALLY))));
            subFlatPayment.where(cb.and(
                    cb.equal(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID), root.get(Flat_.ID)),
                    cb.isNotNull(subRoot.get(FlatPayment_.ACTUALLY)),
                    cb.isFalse(subRoot.get(FlatPayment_.DELETED))
            ));
            subFlatPayment.groupBy(subRoot.get(FlatPayment_.FLAT).get(Flat_.ID));

            return cb.between(subFlatPayment, finalDebtStart, finalDebtFin);
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
    public static Specification<Flat> likeContractId(Long contractId) {
        if (contractId == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Flat_.CONTRACT).get(Contract_.ID), contractId);
        };
    }
    public static Specification<Flat> likeBuyer(String buyer) {
        if (buyer == null || buyer.equals("")) {
            return null;
        }

        String[] words = buyer.split(" ");

        return (root, query, builder) -> {
            var flat = root.get(Flat_.BUYER);

            List<Predicate> predicates = new ArrayList<>();
            if(words.length == 1){
                predicates.add(builder.like(flat.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                predicates.add(builder.like(flat.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                return builder.or(predicates.toArray(new Predicate[0]));
            } else if(words.length == 2){
                predicates.add(builder.and(
                        builder.like(flat.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(flat.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(flat.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(flat.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%")));

                return builder.or(predicates.toArray(new Predicate[0]));
            } else {
                return builder.equal(root.get(Flat_.ID), 0);
            }
        };
    }
    public static Specification<Flat> likeRealtor(String realtor) {
        if (realtor == null || realtor.equals("")) {
            return null;
        }

        String[] words = realtor.split(" ");

        return (root, query, builder) -> {
            var flat = root.get(Flat_.REALTOR);

            List<Predicate> predicates = new ArrayList<>();
            if(words.length == 1){
                predicates.add(builder.like(flat.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                predicates.add(builder.like(flat.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                return builder.or(predicates.toArray(new Predicate[0]));
            } else if(words.length == 2){
                predicates.add(builder.and(
                        builder.like(flat.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(flat.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(flat.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(flat.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%")));

                return builder.or(predicates.toArray(new Predicate[0]));
            } else {
                return builder.equal(root.get(Flat_.ID), 0);
            }
        };
    }

    public static Specification<Flat> deletedFalse() {
        return (root, query, cb) -> {
            return cb.isFalse(root.get(Flat_.DELETED));
        };
    }

    public static Specification<Flat> likeSale(Double sale) {
        if (sale == null){
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(cb.diff(root.get(Flat_.PRICE), root.get(Flat_.SALE_PRICE)), sale);
        };
    }
}
