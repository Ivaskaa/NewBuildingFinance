package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.entities.object.StatusObject;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ObjectDto {
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String house;
    @NotEmpty(message = "Must not be empty")
    private String section;
    @NotEmpty(message = "Must not be empty")
    private String address;
    @NotNull(message = "Choose something")
    private StatusObject status;
    @NotEmpty(message = "Must not be empty")
    private String agency = "0";
    @NotEmpty(message = "Must not be empty")
    private String manager = "0";
    private boolean active;

    public Object build(){
        Object object = new Object();
        object.setId(id);
        object.setHouse(house);
        object.setSection(section);
        object.setAddress(address);
        if(status != null) {
            object.setStatus(status);
        }
        if(!agency.equals("")) {
            object.setAgency(Integer.parseInt(agency));
        }
        if(!manager.equals("")) {
            object.setManager(Integer.parseInt(manager));
        }
        object.setActive(active);
        return object;
    }
}
