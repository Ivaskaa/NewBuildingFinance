package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RealtorDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long agencyId;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String name;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String surname;
    @NotEmpty(message = "Must not be empty")
    private String phone;
    @Email(message = "Email must be valid")
    @NotEmpty(message = "Must not be empty")
    private String email;
    private boolean director;

    public Realtor build(){
        Realtor realtor = new Realtor();
        realtor.setId(id);
        realtor.setName(name);
        realtor.setSurname(surname);
        realtor.setPhone(phone);
        realtor.setEmail(email);
        realtor.setDirector(director);
        return realtor;
    }

}
