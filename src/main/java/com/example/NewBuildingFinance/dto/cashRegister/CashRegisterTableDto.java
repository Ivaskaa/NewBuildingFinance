package com.example.NewBuildingFinance.dto.cashRegister;

import lombok.Data;

import java.util.Date;

@Data
public class CashRegisterTableDto {
    private Long id;
    private Long number;
    private Date date;
    private String economic;
    private String status;
    private String object;
    private String article; // стаття
    private Double price;
    private String currency; // валюта
    private String counterparty; // контрагент
}
