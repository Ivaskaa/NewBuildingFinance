package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.Contract_;
import com.example.NewBuildingFinance.entities.flat.Flat_;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContractSpecification {
    public static Specification<Contract> likeId(Long id) {
        if (id == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Contract_.ID), id);
        };
    }
    public static Specification<Contract> likeDate(String dateStartString, String dateFinString) throws ParseException {
        if (dateStartString.equals("") && dateFinString.equals("")) {
            return null;
        }
        Date dateStart = new Date(Long.MIN_VALUE);
        Date dateFin = new Date(99999999999999L);
//        Date dateFin = new Date(Long.MAX_VALUE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (!dateStartString.equals("")){
            dateStart = format.parse(dateStartString);
        }
        if (!dateFinString.equals("")){
            dateFin = format.parse(dateFinString);
        }
        Date finalDateStart = dateStart;
        Date finalDateFin = dateFin;
        return (root, query, cb) -> {
            return cb.between(root.get(Contract_.DATE), finalDateStart, finalDateFin);
        };
    }
    public static Specification<Contract> likeObject(Long objectId) {
        if (objectId == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Contract_.FLAT).get(Flat_.OBJECT).get(Object_.ID), objectId);
        };
    }
    public static Specification<Contract> likeFlatNumber(Integer number) {
        if (number == null) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.equal(root.get(Contract_.FLAT_NUMBER), number);
        };
    }
    public static Specification<Contract> likeBuyerName(String name) {
        if (name == null || name.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(name.contains(" ")) {
                String[] words = name.split(" ");
                for (String word : words) {
                    predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.NAME), "%" + word.toLowerCase(Locale.ROOT) + "%"));
                    predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.SURNAME), "%" + word.toLowerCase(Locale.ROOT) + "%"));
                    predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.LASTNAME), "%" + word.toLowerCase(Locale.ROOT) + "%"));
                }
            } else {
                predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.NAME), "%" + name.toLowerCase(Locale.ROOT) + "%"));
                predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.SURNAME), "%" + name.toLowerCase(Locale.ROOT) + "%"));
                predicates.add(cb.like(root.get(Contract_.FLAT).get(Flat_.BUYER).get(Buyer_.LASTNAME), "%" + name.toLowerCase(Locale.ROOT) + "%"));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Contract> likeComment(String comment) {
        if (comment == null || comment.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.like(root.get(Contract_.COMMENT), "%" + comment.toLowerCase(Locale.ROOT) + "%");
        };
    }
    public static Specification<Contract> likeDeletedFalse() {
        return (root, query, cb) -> {
            return cb.equal(root.get(Contract_.DELETED), false);
        };
    }
}
