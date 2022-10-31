package com.example.NewBuildingFinance.dto.contract;

import lombok.Data;

import java.util.Date;

@Data
public class ContractTableDto {
    private Long id;
    private Date date;
    private Integer flatNumber;
    private String object;
    private String buyer;
    private String comment;
}
