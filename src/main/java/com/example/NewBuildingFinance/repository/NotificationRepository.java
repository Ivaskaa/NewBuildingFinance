package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAll(Pageable pageable);
    List<Notification> findAllByInListTrue();
    Optional<Notification> findByContractId(Long contract_id);
    List<Notification> findAllByInListTrueAndReviewedTrue();

    Notification findByAgencyId(Long agencyId);
}
