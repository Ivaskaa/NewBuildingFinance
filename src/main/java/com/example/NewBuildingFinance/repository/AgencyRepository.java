package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long>, JpaSpecificationExecutor<Agency> {
    Page<Agency> findAll(Specification<Agency> specification, Pageable pageable);
    Agency findByName (String name);
}

