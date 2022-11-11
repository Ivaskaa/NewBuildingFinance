package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.controllers.WebSocketsController;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.notification.Notification;
import com.example.NewBuildingFinance.repository.FlatPaymentRepository;
import com.example.NewBuildingFinance.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
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

//    public Page<ObjectTableDto> findSortingPage(
//            Integer currentPage,
//            Integer size,
//            String sortingField,
//            String sortingDirection) {
//        log.info("get object page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
//        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
//        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
//        Page<ObjectTableDto> objectPage = objectRepository.findAll(pageable).map(Object::build);
//        log.info("success");
//        return objectPage;
//    }

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

    public void updateNotifications() throws Exception {
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
        for (FlatPayment flatPayment : flatPayments) {
            if(flatPayment.getFlat().getContract() != null) {
                Notification notification = new Notification();
                notification.setName(flatPayment.getPlanned().intValue() + " owed payment: "
                        + flatPayment.getFlat().getBuyer().getSurname() + " "
                        + flatPayment.getFlat().getBuyer().getName());
                notification.setFlatPayment(flatPayment);
                notification.setUrl("/flats/flat/" + flatPayment.getFlat().getId() + "?flatPaymentId=" + flatPayment.getId());
                notifications.add(notification);
            }
        }
        notificationRepository.saveAll(notifications);
        template.convertAndSend("/topic/notifications", "Hello");
        log.info("success update notification");
    }
}
