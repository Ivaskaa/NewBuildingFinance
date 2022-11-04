package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long>, JpaSpecificationExecutor<CashRegister>  {
    Page<CashRegister> findAll(Specification<CashRegister> specification, Pageable pageable);

    CashRegister findByNumber(Long number);

    Optional<CashRegister> findByFlatPaymentId(Long flatPaymentId);

    Optional<CashRegister> findByFlatId(Long flatId);
}
