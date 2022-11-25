package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.contract.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
//    @Query("select c from Contract c where c.deleted = false")

    Page<Contract> findAll(Specification<Contract> specification, Pageable pageable);

    Page<Contract> findAllByBuyerId(Pageable pageable, Long buyerId);

    Page<Contract> findAllByFlatRealtorAgencyId(Pageable pageable, Long agencyId);
}
