package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FlatPaymentRepository extends JpaRepository<FlatPayment, Long> {
    List<FlatPayment> findAllByFlatId(Long flatId);
    Page<FlatPayment> findAllByFlatId(Pageable pageable, Long flatId);
    @Query("select f from FlatPayment f where f.number = ?1 and f.flat.id = ?2")
    FlatPayment findByNumberAndFlatId(Long number, Long flatId);
}