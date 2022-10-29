package com.example.NewBuildingFinance.dto.flat;

import lombok.Data;

import java.util.Date;
@Data
public class FlatPaymentTableDto {
    private Long id;
    private Long number;
    private Date date;
    private Integer planned;
    private Integer actually;
    private Integer remains;
}
