package com.example.NewBuildingFinance.dto.cashRegister;

import com.example.NewBuildingFinance.entities.cashRegister.Article;
import lombok.Data;

import java.util.Date;

@Data
public class SpendingUploadDto {
    private Long id;
    private Long number;
    private Date date;
    private Long objectId;
    private Long flatId;
    private Long managerId;
    private Long realtorId;
    private String counterparty;
    private Article article; // стаття
    private Double price;
    private Long currencyId; // валюта
    private Double admissionCourse;
    private String comment;
}
