package com.example.NewBuildingFinance.service.notification;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.notification.Notification;

import java.util.List;

public interface NotificationService {

    /**
     * create notification from contract
     * @param contract contract
     */
    void createNotificationFromContract(Contract contract);

    /**
     * update notification from contract
     * @param contract contract
     */
    void updateNotificationFromContract(Contract contract);

    /**
     * delete notification by contract id
     * @param contractId contract id
     */
    void deleteNotificationByContractId(Long contractId);

    /**
     * create notification from agency
     * @param agency contract id
     */
    void createNotificationFromAgency(Agency agency);

    /**
     * delete notification by agency id
     * @param agencyId agency id
     */
    void deleteNotificationByAgencyId(Long agencyId);

    /**
     * find all where InList true
     * @return list of notifications
     */
    List<Notification> findAll();

    /**
     * save notification
     * @param object notification
     * @return notification
     */
    Notification save(Notification object);

    /**
     * find notification by id
     * @param id id
     * @return notification
     */
    Notification findById(Long id);

    /**
     * find notification by contract id
     * @param id contract id
     * @return notification
     */
    Notification findByContractId(Long id);

    /**
     * set reviewed true by id
     * @param id notification id
     * @return notification
     */
    Notification setReviewed(Long id);

    /**
     * update notifications (where reviewed was true set in list false)
     * if notification about flat payment was reviewed but not paid in list was true
     */
    void updateNotifications();
}
