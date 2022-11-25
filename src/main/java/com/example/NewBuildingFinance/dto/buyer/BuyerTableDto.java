package com.example.NewBuildingFinance.dto.buyer;

import lombok.Data;

@Data
public class BuyerTableDto {
    private Long id;
    private String name;
    private Integer count;
    private String surname;
    private String lastname;
    private String phone;
    private String email;
    private String agency;
    private String realtor;
    private String manager;
}
