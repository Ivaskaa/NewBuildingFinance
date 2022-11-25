package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.Contract_;
import com.example.NewBuildingFinance.entities.flat.Flat_;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.Object_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
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
        String[] words = name.split(" ");
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Path<Object> path = root.get(Contract_.FLAT).get(Flat_.BUYER);

            if(words.length == 1){
                predicates.add(builder.like(path.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                predicates.add(builder.like(path.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                predicates.add(builder.like(path.get(Buyer_.LASTNAME),"%" +  words[0].toLowerCase(Locale.ROOT) + "%"));
                return builder.or(predicates.toArray(new Predicate[0]));
            } else if(words.length == 2){
                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.LASTNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.LASTNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.LASTNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                predicates.add(builder.and(
                        builder.like(path.get(Buyer_.LASTNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(path.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%")));

                return builder.or(predicates.toArray(new Predicate[0]));
            } else if(words.length == 3){
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        for(int k = 0; k < 3; k++){
                            if(i != j && i != k && j != k){
                                predicates.add(builder.and(
                                        builder.like(path.get(Buyer_.NAME), "%" + words[i].toLowerCase(Locale.ROOT) + "%"),
                                        builder.like(path.get(Buyer_.SURNAME), "%" + words[j].toLowerCase(Locale.ROOT) + "%"),
                                        builder.like(path.get(Buyer_.LASTNAME), "%" + words[k].toLowerCase(Locale.ROOT) + "%")));
                            }
                        }
                    }
                }
                return builder.or(predicates.toArray(new Predicate[0]));
            } else {
                return builder.equal(root.get(Contract_.ID), 0);
            }
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
