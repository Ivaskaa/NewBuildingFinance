package com.example.NewBuildingFinance.dto.cashRegister;

import lombok.Data;

import java.util.Date;

@Data
public class CashRegisterTableDtoForFlat {
    private Long id;
    private String counterparty;
    private String type;
    private String manager;
    private String status;
    private Integer percent;
    private Double price;
    private Date date;
}
