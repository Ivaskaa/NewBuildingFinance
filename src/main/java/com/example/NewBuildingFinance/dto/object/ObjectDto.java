package com.example.NewBuildingFinance.dto.object;

import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.StatusObject;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ObjectDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    private String house;
    @NotBlank(message = "Must not be empty")
    private String section;
    @NotBlank(message = "Must not be empty")
    private String address;
    @NotNull(message = "Choose something")
    private StatusObject status;
    @NotNull(message = "Must not be empty")
    private Integer agency;
    @NotNull(message = "Must not be empty")
    private Integer manager;

    public Object build(){
        Object object = new Object();
        object.setId(id);
        object.setHouse(house);
        object.setSection(section);
        object.setAddress(address);
        object.setStatus(status);
        object.setAgency(agency);
        object.setManager(manager);
        return object;
    }
}
