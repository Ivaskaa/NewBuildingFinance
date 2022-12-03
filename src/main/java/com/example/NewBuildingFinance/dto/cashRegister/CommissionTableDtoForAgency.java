package com.example.NewBuildingFinance.dto.cashRegister;

import lombok.Data;

import java.util.Date;

@Data
public class CommissionTableDtoForAgency {
    private Long flatId;
    private Integer flatNumber;
    private String object;
    private Double flatArea;
    private Double price;
    private String status;
    private Integer percent;
    private Double money;
    private Date date;
}
