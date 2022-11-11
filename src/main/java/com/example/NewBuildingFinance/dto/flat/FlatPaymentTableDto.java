package com.example.NewBuildingFinance.dto.flat;

import lombok.Data;

import java.util.Date;
@Data
public class FlatPaymentTableDto {
    private Long id;
    private Integer number;
    private Date date;
    private Double planned;
    private Double actually;
    private Double remains;
}
