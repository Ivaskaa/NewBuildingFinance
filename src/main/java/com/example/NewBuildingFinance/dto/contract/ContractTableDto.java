package com.example.NewBuildingFinance.dto.contract;

import lombok.Data;

@Data
public class ContractTableDto {
    private Long id;
    private String date;
    private Integer flatNumber;
    private String object;
    private String buyer;
    private String comment;
}
