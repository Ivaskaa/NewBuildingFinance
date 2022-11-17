package com.example.NewBuildingFinance.service.notification;

import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.notification.Notification;

import java.util.List;

public interface NotificationService {

    void createNotificationFromContract(Contract contract);

    void updateNotificationFromContract(Contract contract);

    void createNotificationFromAgency(Long agencyId);

    List<Notification> findAll();

    Notification save(Notification object);

    Notification findById(Long id);

    Notification findByContractId(Long id);

    Notification setReviewed(Long id);

    void updateNotifications();
}
