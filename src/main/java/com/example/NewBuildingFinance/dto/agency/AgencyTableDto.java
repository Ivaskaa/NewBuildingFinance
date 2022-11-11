package com.example.NewBuildingFinance.dto.agency;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class AgencyTableDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String directorName;
    private String directorPhone;
    private String directorEmail;
    private Integer count; //sale count
}
