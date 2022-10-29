package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealtorRepository extends JpaRepository<Realtor, Long> {
    Page<Realtor> findAllByAgencyId(Pageable pageable, Long id);
    @Query("Select r from Realtor r where r.agency.id = :agencyId and r.director = true")
    Realtor findDirectorByAgencyId(@Param("agencyId") Long agencyId);
    List<Realtor> findAllByAgencyId(Long id);
}
