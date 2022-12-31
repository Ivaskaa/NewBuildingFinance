package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.dto.cashRegister.CommissionTableDtoForAgency;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long>, JpaSpecificationExecutor<CashRegister>  {
    Page<CashRegister> findAll(Specification<CashRegister> specification, Pageable pageable);

    CashRegister findByNumber(Long number);

    Optional<CashRegister> findByFlatPaymentId(Long flatPaymentId);

    Optional<CashRegister> findByFlatId(Long flatId);

    List<CashRegister> findAllByFlatIdAndDeletedFalse(Long id);

    Page<CashRegister> findAllByFlatRealtorAgencyIdAndDeletedFalse(Pageable pageable, Long agencyId);

    @Modifying
    @Query(value = "update newbuildingfinance.cash_registers set cash_registers.deleted = true where cash_registers.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}
