package com.example.NewBuildingFinance.entities.setting;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean notificationContract;
    private boolean notificationFlatPayment;
    private boolean notificationAgency;
    private boolean sendEmailToBuyers;
    @Size(max = 5000, message = "must be less then 5000 characters")
    private String pdfFooter;
}
