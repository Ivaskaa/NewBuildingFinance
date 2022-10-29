package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.Flat_;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

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
    public static Specification<Flat> likeArea(Integer areaStart, Integer areaFin) {
        if (areaStart == null || areaFin == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.AREA), areaStart, areaFin);
        };
    }
    public static Specification<Flat> likePrice(Integer priceStart, Integer priceFin) {
        if (priceStart == null || priceFin == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.PRICE), priceStart, priceFin);
        };
    }
    public static Specification<Flat> likeAdvance(Integer advanceStart, Integer advanceFin) {
        if (advanceStart == null || advanceFin == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.between(root.get(Flat_.ADVANCE), advanceStart, advanceFin);
        };
    }
}
