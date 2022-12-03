package com.example.NewBuildingFinance.dto.buyer;

import lombok.Data;

@Data
public class BuyerUploadDto {
    private Long id;
    private String name;
    private String surname;
    private String lastname;
    private String address;
    private Long idNumber;

    private String documentStyle;

    private String passportSeries;
    private Integer passportNumber;
    private String passportWhoIssued;

    private Long idCardNumber;
    private Integer idCardWhoIssued;

    private String phone;
    private String email;
    private String note;

    private Long realtorId;
    private Long agencyId;
    private Long managerId;
}
