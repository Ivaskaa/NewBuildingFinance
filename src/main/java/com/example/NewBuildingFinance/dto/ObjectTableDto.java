package com.example.NewBuildingFinance.dto;

import lombok.Data;
@Data
public class ObjectTableDto {
    private Long id;
    private String house;
    private String section;
    private String address;
    private String status;
    private Integer agency;
    private Integer manager;
    private boolean active;
}
