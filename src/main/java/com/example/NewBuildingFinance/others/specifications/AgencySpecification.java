package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
import com.example.NewBuildingFinance.entities.buyer.Buyer_;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.Flat_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AgencySpecification {
    public static Specification<Agency> likeName(String name) {
        if (name == null || name.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            return cb.like(root.get(Agency_.NAME), "%" + name.toLowerCase(Locale.ROOT) + "%");
        };
    }
    public static Specification<Agency> likeDirector(String director) {
        if (director.equals("")) {
            return null;
        }

        String[] words = director.split(" ");

        return (root, query, builder) -> {
            Join<Object, Object> bListJoin = root.join(Agency_.REALTORS, JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            if(words.length == 1){
                predicates.add(builder.and(
                        builder.like(bListJoin.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.isTrue(bListJoin.get(Realtor_.DIRECTOR))));

                predicates.add(builder.and(
                        builder.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.isTrue(bListJoin.get(Realtor_.DIRECTOR))));
                return builder.or(predicates.toArray(new Predicate[0]));
            } else if(words.length == 2){
                predicates.add(builder.and(
                        builder.like(bListJoin.get(Buyer_.NAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
                        builder.isTrue(bListJoin.get(Realtor_.DIRECTOR))));

                predicates.add(builder.and(
                        builder.like(bListJoin.get(Buyer_.NAME), "%" +  words[1].toLowerCase(Locale.ROOT) + "%"),
                        builder.like(bListJoin.get(Buyer_.SURNAME), "%" +  words[0].toLowerCase(Locale.ROOT) + "%"),
                        builder.isTrue(bListJoin.get(Realtor_.DIRECTOR))));

                return builder.or(predicates.toArray(new Predicate[0]));
            } else {
                return builder.equal(root.get(Agency_.ID), 0);
            }
        };
    }
    public static Specification<Agency> likePhone(String phone) {
        if (phone.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Object, Object> bListJoin = root.join(Agency_.REALTORS, JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(bListJoin.get(Realtor_.PHONE), "%" + phone.toLowerCase(Locale.ROOT) + "%"));
            predicates.add(cb.isTrue(bListJoin.get(Realtor_.DIRECTOR)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Agency> likeEmail(String email) {
        if (email.equals("")) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Object, Object> bListJoin = root.join(Agency_.REALTORS, JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(bListJoin.get(Realtor_.EMAIL), "%" + email.toLowerCase(Locale.ROOT) + "%"));
            predicates.add(cb.isTrue(bListJoin.get(Realtor_.DIRECTOR)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Agency> likeCount(Integer count) {
        if (count == null) {
            return null;
        }
        return (root, query, cb) -> {
            // fixme
            Join<Object, Object> agencyRealtor = root.join(Agency_.REALTORS, JoinType.INNER);
            Join<Object, Object> agencyRealtorFlat = agencyRealtor.join(Realtor_.FLATS, JoinType.INNER);

            return cb.equal(
                        cb.count(
                                query.subquery(Realtor_.class).select(agencyRealtor.get(Realtor_.FLATS))
                        ), count);
        };
    }
}
