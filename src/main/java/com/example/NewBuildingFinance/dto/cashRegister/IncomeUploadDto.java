package com.example.NewBuildingFinance.dto.cashRegister;

import com.example.NewBuildingFinance.entities.cashRegister.Article;
import lombok.Data;

import java.util.Date;

@Data
public class IncomeUploadDto {
    private Long id;
    private Long number;
    private Date date;
    private boolean status;
    private Long objectId;
    private Long flatId;
    private Long flatPaymentId;
    private Article article; // стаття
    private Double price;
    private Long currencyId; // валюта
    private Double admissionCourse;
    private String comment;
}
