package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RealtorRepository extends JpaRepository<Realtor, Long> {
    Page<Realtor> findAllByAgencyIdAndDeletedFalse(Pageable pageable, Long agency_id);
    @Query("Select r from Realtor r where r.agency.id = :agencyId and r.director = true")
    Realtor findDirectorByAgencyId(@Param("agencyId") Long agencyId);
    List<Realtor> findAllByAgencyIdAndDeletedFalseOrAgencyIdAndId(Long agency_id, Long agency_id2, Long id);
    List<Realtor> findAllByAgencyIdAndDeletedFalse(Long agency_id);

    @Modifying
    @Query(value = "update newbuildingfinance.realtors set realtors.deleted = true, realtors.director = false where realtors.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}
