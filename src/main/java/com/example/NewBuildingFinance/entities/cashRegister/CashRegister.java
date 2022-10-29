package com.example.NewBuildingFinance.entities.cashRegister;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cash_registers")
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    @JoinColumn(name = "economic_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Economic economic;
    @Enumerated(EnumType.ORDINAL)
    private StatusCashRegister status;
//    @JoinColumn(name = "status_id")
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
//    @JsonBackReference
//    private StatusCashRegister status;
    @JoinColumn(name = "flat_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Flat flat;
    @Enumerated(EnumType.STRING)
    private Article article; // стаття
    @JoinColumn(name = "currency_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private InternalCurrency currency; // валюта
    private Double price;
    private boolean completed;
    private Integer paymentNumber;
}
