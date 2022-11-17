package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.repository.NotificationRepository;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import com.example.NewBuildingFinance.service.setting.SettingServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;
    private final FlatPaymentRepository flatPaymentRepository;
    private final SettingServiceImpl settingService;
    private final BuyerServiceImpl buyerServiceImpl;

    public void createNotificationFromContract(Contract contract) {
        if(settingService.getSettings().isNotificationContract()) {
            Notification notification = new Notification();
            Buyer buyer = buyerServiceImpl.findById(contract.getBuyer().getId());
            notification.setName("New contract. buyer: " + buyer.getSurname() + " " + buyer.getName());
            notification.setContract(contract);
            notification.setUrl("/contracts/contract/" + contract.getId());
            save(notification);
            template.convertAndSend("/topic/notifications", "Hello");
        }
    }

    public void updateNotificationFromContract(Contract contract) {
        if(settingService.getSettings().isNotificationContract()) {
            Notification notification = findByContractId(contract.getId());
            Buyer buyer = buyerServiceImpl.findById(contract.getBuyer().getId());
            notification.setName("Update contract. buyer: " + buyer.getSurname() + " " + buyer.getName());
            notification.setContract(contract);
            notification.setUrl("/contracts/contract/" + contract.getId());
            notification.setReviewed(false);
            notification.setInList(true);
            save(notification);
            template.convertAndSend("/topic/notifications", "Hello");
        }
    }

    public void createNotificationFromAgency(Long agencyId) {
        if(settingService.getSettings().isNotificationAgency()) {
            Notification notification = new Notification();
            notification.setName("New agency");
            notification.setUrl("/agencies/agency/" + agencyId);
            save(notification);
            template.convertAndSend("/topic/notifications", "Hello");
        }
    }

    public List<Notification> findAll() {
        log.info("get all not reviewed notifications");
        List<Notification> notifications = notificationRepository.findAllByInListTrue();
        log.info("success get all not reviewed notifications");
        return notifications;
    }

    public Notification save(Notification object) {
        log.info("save notification: {}", object);
        notificationRepository.save(object);
        log.info("success save notification");
        return object;
    }

    public Notification findById(Long id) {
        log.info("get notification by id: {}", id);
        Notification notification = notificationRepository.findById(id).orElseThrow();
        log.info("success get notification by id");
        return notification;
    }

    public Notification findByContractId(Long id) {
        log.info("get notification by id: {}", id);
        Notification notification = notificationRepository.findByContractId(id).orElseThrow();
        log.info("success get notification by id");
        return notification;
    }

    public Notification setReviewed(Long id) {
        log.info("set reviewed by id: {}", id);
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setReviewed(true);
        notificationRepository.save(notification);
        log.info("success set reviewed by id");
        return notification;
    }

    public void updateNotifications() {
        log.info("update notification");
        List<Notification> notifications = notificationRepository.findAllByInListTrue();
        List<FlatPayment> flatPayments = flatPaymentRepository.findAllArrears();
        for(Notification notification: notifications){
            if(notification.isReviewed()){
                notification.setInList(false);
                continue;
            }
            if(notification.getFlatPayment() != null) {
                flatPayments.removeIf(flatPayment -> Objects.equals(flatPayment.getId(), notification.getFlatPayment().getId()));
            }
        }
        if(settingService.getSettings().isNotificationFlatPayment()) {
            for (FlatPayment flatPayment : flatPayments) {
                if (flatPayment.getFlat().getContract() != null) {
                    Notification notification = new Notification();
                    notification.setName(flatPayment.getPlanned().intValue() + " owed payment: "
                            + flatPayment.getFlat().getBuyer().getSurname() + " "
                            + flatPayment.getFlat().getBuyer().getName());
                    notification.setFlatPayment(flatPayment);
                    notification.setUrl("/flats/flat/" + flatPayment.getFlat().getId() + "?flatPaymentId=" + flatPayment.getId());
                    notifications.add(notification);
                }
            }
        }
        notificationRepository.saveAll(notifications);
        template.convertAndSend("/topic/notifications", "Hello");
        log.info("success update notification");
    }


}
