package com.example.NewBuildingFinance.dto.object;

import com.example.NewBuildingFinance.entities.flat.StatusFlat;
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
//    @NotEmpty(message = )
//    private String status;
    @NotNull(message = "Must not be empty")
    private Integer agency;
    @NotNull(message = "Must not be empty")
    private Integer manager;
    private boolean active;

    public Object build(){
        Object object = new Object();
        object.setId(id);
        object.setHouse(house);
        object.setSection(section);
        object.setAddress(address);
//        if(status != null && !status.equals("")) {
//            object.setStatus(StatusObject.valueOf(status));
//        }
        object.setStatus(status);
        object.setAgency(agency);
        object.setManager(manager);
        object.setActive(active);
        return object;
    }
}
