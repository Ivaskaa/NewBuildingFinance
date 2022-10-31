package com.example.NewBuildingFinance.dto.contract;

import lombok.Data;

import java.util.Date;

@Data
public class ContractTableDtoForAgency {
    private Long id;
    private Long flatId;
    private Integer flatNumber;
    private Double flatArea;
    private String status;
    private Integer price;
    private String object;
    private Date date;
    private String buyer;
}