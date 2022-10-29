package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.agency.Agency;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AgencyDto {
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String name;
    private String description;

    public Agency build(){
        Agency agency = new Agency();
        agency.setId(id);
        agency.setName(name);
        agency.setDescription(description);
        return agency;
    }
}
