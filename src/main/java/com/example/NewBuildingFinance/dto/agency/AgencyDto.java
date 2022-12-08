package com.example.NewBuildingFinance.dto.agency;

import com.example.NewBuildingFinance.entities.agency.Agency;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class AgencyDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
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
