package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.flat.Flat;
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

import java.util.List;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long>, JpaSpecificationExecutor<Agency> {
    Page<Agency> findAll(Specification<Agency> specification, Pageable pageable);
    Agency findByName (String name);

    List<Agency> findAllByDeletedFalse();

    List<Agency> findAllByDeletedFalseOrId(Long agencyId);

    @Modifying
    @Query(value = "update newbuildingfinance.agencies set agencies.deleted = true where agencies.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}

