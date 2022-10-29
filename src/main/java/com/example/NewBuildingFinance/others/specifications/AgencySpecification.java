package com.example.NewBuildingFinance.others.specifications;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Agency_;
import com.example.NewBuildingFinance.entities.agency.Realtor_;
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
        return (root, query, cb) -> {
            Join<Object, Object> bListJoin = root.join(Agency_.REALTORS, JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(bListJoin.get(Realtor_.NAME), "%" + director.toLowerCase(Locale.ROOT) + "%"));
            predicates.add(cb.isTrue(bListJoin.get(Realtor_.DIRECTOR)));
            return cb.and(predicates.toArray(new Predicate[0]));
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
            return cb.equal(root.get(Agency_.COUNT), count);
        };
    }
}
