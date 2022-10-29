package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long>, JpaSpecificationExecutor<Flat> {

    Page<Flat> findAll(Specification<Flat> specification, Pageable pageable);

    @Query("Select f from Flat f where f.number = ?1 and f.object.id = ?2")
    Flat findFlatInObject(Integer flat, Long object);

    Optional<Flat> findFlatByContractId(Long id);


    List<Flat> findAllByDeletedFalseAndObjectIdAndContractNullAndStatus(Long object_id, StatusFlat status);
    List<Flat> findAllByDeletedFalseAndObjectIdAndContractNullAndStatusOrId(Long object_id, StatusFlat status, Long id);
}
