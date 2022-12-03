package com.example.NewBuildingFinance.entities.cashRegister;

import com.example.NewBuildingFinance.dto.cashRegister.*;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.service.internalCurrency.InternalCurrencyServiceImpl;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "cash_registers")
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long number;
    private Date date;

    @Enumerated(EnumType.STRING)
    private Economic economic;

    @Enumerated(EnumType.STRING)
    private StatusCashRegister status;

    @JoinColumn(name = "object_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Object object;

    @JoinColumn(name = "flat_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Flat flat;

    @JoinColumn(name = "flat_payment_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private FlatPayment flatPayment;

    @Enumerated(EnumType.STRING)
    private Article article; // стаття

    private Double price;

    @JoinColumn(name = "currency_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private InternalCurrency currency; // валюта

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private User manager;

    @JoinColumn(name = "realtor_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Realtor realtor;

    private String counterparty; // контрагент
    private Double admissionCourse;
    private String comment;
    private boolean deleted = false;

    public CashRegisterTableDto build(){
        CashRegisterTableDto cashRegister = new CashRegisterTableDto();
        cashRegister.setId(id);
        cashRegister.setNumber(number);
        cashRegister.setDate(date);
        cashRegister.setEconomic(economic.getValue());
        cashRegister.setStatus(status.getValue());
        cashRegister.setArticle(article.getValue());
        cashRegister.setPrice(price);
        cashRegister.setCurrency(currency.getName());
        if(object != null){
            cashRegister.setObject(
                    object.getHouse() + "(" +
                    object.getSection() + ")");
        }
        cashRegister.setCounterparty(counterparty);
        return cashRegister;
    }

    public CashRegisterTableDtoForFlat buildForFlat(){
        CashRegisterTableDtoForFlat cashRegister = new CashRegisterTableDtoForFlat();
        cashRegister.setId(id);
        if(realtor != null){
            cashRegister.setCounterparty(realtor.getAgency().getName());
            cashRegister.setType("Agency");
            cashRegister.setManager(realtor.getSurname() + " " + realtor.getName());
            cashRegister.setPercent(flat.getAgency());
        } else {
            cashRegister.setCounterparty("Sales department");
            cashRegister.setType("Sales department");
            cashRegister.setManager(manager.getSurname() + " " + manager.getName());
            cashRegister.setPercent(flat.getManager());
        }
        cashRegister.setStatus(status.getValue());
        cashRegister.setPrice(price);
        cashRegister.setCurrency(currency.getName());
        cashRegister.setDate(date);
        return cashRegister;
    }

    public CommissionTableDtoForAgency buildCommissionTableDtoForAgency(){
        CommissionTableDtoForAgency commissionTableDtoForAgency = new CommissionTableDtoForAgency();
        commissionTableDtoForAgency.setFlatArea(flat.getArea());
        commissionTableDtoForAgency.setFlatId(flat.getId());
        commissionTableDtoForAgency.setMoney(price);
        commissionTableDtoForAgency.setDate(date);
        commissionTableDtoForAgency.setFlatNumber(flat.getNumber());
        commissionTableDtoForAgency.setObject(flat.getObject().getHouse() + "(" + flat.getObject().getSection() + ")");
        commissionTableDtoForAgency.setStatus(status.getValue());
        commissionTableDtoForAgency.setPercent(flat.getAgency());
        commissionTableDtoForAgency.setPrice(flat.getSalePrice());
        return commissionTableDtoForAgency;
    }


    public IncomeUploadDto buildIncomeUploadDto(){
        IncomeUploadDto cashRegister = new IncomeUploadDto();
        cashRegister.setId(id);
        cashRegister.setNumber(number);
        cashRegister.setDate(date);
        cashRegister.setStatus(status.equals(StatusCashRegister.COMPLETED));
        if(object != null) {
            cashRegister.setObjectId(object.getId());
        }
        if(flatPayment != null) {
            if(flatPayment.getFlat()!= null) {
                cashRegister.setFlatId(flatPayment.getFlat().getId());
            }
            cashRegister.setFlatPaymentId(flatPayment.getId());
        }
        cashRegister.setArticle(article);
        cashRegister.setPrice(price);
        cashRegister.setCurrencyId(currency.getId());
        cashRegister.setAdmissionCourse(admissionCourse);
        cashRegister.setComment(comment);
        return cashRegister;
    }

    public SpendingUploadDto buildSpendingUploadDto() {
        SpendingUploadDto cashRegister = new SpendingUploadDto();
        cashRegister.setId(id);
        cashRegister.setNumber(number);
        cashRegister.setDate(date);
        if (status.equals(StatusCashRegister.COMPLETED)){
            cashRegister.setCompleted(true);
        } else if (status.equals(StatusCashRegister.PLANNED)){
            cashRegister.setCompleted(false);
        }
        if(manager != null){
            cashRegister.setManagerId(manager.getId());
        } else if(realtor != null){
            cashRegister.setAgencyId(realtor.getAgency().getId());
            cashRegister.setRealtorId(realtor.getId());
        } else {
            cashRegister.setCounterparty(counterparty);
        }
        cashRegister.setArticle(article);
        if(object != null) {
            cashRegister.setObjectId(object.getId());
        }
        if(flat != null) {
            cashRegister.setFlatId(flat.getId());
        }
        cashRegister.setArticle(article);
        cashRegister.setPrice(price);
        cashRegister.setCurrencyId(currency.getId());
        cashRegister.setAdmissionCourse(admissionCourse);
        cashRegister.setComment(comment);
        return cashRegister;
    }

    @Override
    public String toString() {
        return "CashRegister{" +
                "id=" + id +
                ", number=" + number +
                ", date=" + date +
                ", economic=" + economic +
                ", status=" + status +
                ", object=" + object +
                ", flatPayment=" + flatPayment +
                ", article=" + article +
                ", price=" + price +
                ", currency=" + currency +
                ", manager=" + manager +
                ", realtor=" + realtor +
                ", counterparty='" + counterparty + '\'' +
                ", admissionCourse=" + admissionCourse +
                ", comment='" + comment + '\'' +
                '}';
    }


}
