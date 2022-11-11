package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.notification.Notification;
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
    FlatPayment findByNumberAndFlatId(Integer number, Long flatId);

    List<FlatPayment> findByFlatIdAndPaidFalse(Long flat_id);
    List<FlatPayment> findByFlatIdAndPaidFalseOrId(Long flat_id, Long id);

    List<FlatPayment> findByFlatId(Long flat_id);

    @Query("SELECT fp FROM FlatPayment fp WHERE fp.date < CURRENT_DATE and fp.paid = false")
    List<FlatPayment> findAllArrears();
}