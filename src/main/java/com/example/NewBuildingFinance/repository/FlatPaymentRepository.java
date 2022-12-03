package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface FlatPaymentRepository extends JpaRepository<FlatPayment, Long> {

    Page<FlatPayment> findAllByFlatIdAndDeletedFalse(Pageable pageable, Long flatId);

    FlatPayment findByNumberAndFlatIdAndDeletedFalse(Integer number, Long flatId);

    List<FlatPayment> findAllByFlatIdAndDeletedFalse(Sort sort, Long flatId);

    List<FlatPayment> findAllByFlatIdAndDeletedFalse(Long flat_id);
    List<FlatPayment> findAllByFlatIdAndPaidFalseAndDeletedFalse(Long flat_id);
    List<FlatPayment> findAllByFlatIdAndPaidFalseAndDeletedFalseOrFlatIdAndId(Long flat_id, Long flat_id2, Long id);

    @Query("SELECT fp FROM FlatPayment fp WHERE fp.date < CURRENT_DATE and fp.paid = false")
    List<FlatPayment> findAllArrears();

    @Modifying
    @Query(value = "update newbuildingfinance.flat_payments set flat_payments.deleted = true, flat_payments.flat_id = null where flat_payments.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);


}