package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    List<Flat> findAllByDeletedFalseAndObjectIdAndContractNotNull(Long object_id);
    List<Flat> findAllByDeletedFalseAndObjectIdAndContractNotNullOrObjectIdAndId(Long object_id, Long object_id2, Long id);


    List<Flat> findAllByObjectIdAndDeletedFalse(Long objectId);
    List<Flat> findAllByDeletedFalse();

    @Modifying
    @Query(value = "update flats set flats.deleted = true where flats.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);

    @Query("Select f.number from Flat f where f.id = ?1")
    Integer findFlatNumberById(Long id);
}
