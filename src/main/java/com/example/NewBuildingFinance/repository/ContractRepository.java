package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.contract.Contract;
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

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    Page<Contract> findAll(Specification<Contract> specification, Pageable pageable);

    Page<Contract> findAllByBuyerIdAndDeletedFalse(Pageable pageable, Long buyerId);

    Page<Contract> findAllByFlatRealtorAgencyIdAndDeletedFalse(Pageable pageable, Long agencyId);

    @Modifying
    @Query(value = "update newbuildingfinance.contracts set contracts.deleted = true, contracts.flat_id = null where contracts.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}
