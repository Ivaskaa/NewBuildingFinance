package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BuyerDto {
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String name;
    @NotEmpty(message = "Must not be empty")
    private String surname;
    @NotEmpty(message = "Must not be empty")
    private String lastname;
    @NotEmpty(message = "Must not be empty")
    private String address;
    @NotNull(message = "Must not be empty")
    private Long idNumber;
    @NotNull(message = "Must not be empty")
    private String passportSeries;
    @NotNull(message = "Must not be empty")
    private Integer passportNumber;
    @NotNull(message = "Must not be empty")
    private Integer passportWhoIssued;
    @NotEmpty(message = "Must not be empty")
    private String phone;
    @NotEmpty(message = "Must not be empty")
    @Email(message = "Must be valid")
    private String email;
    private String note;
    @NotNull(message = "Must not be empty")
    private Long realtor;
    @NotNull(message = "Must not be empty")
    private Long manager; // user

    public Buyer build(){
        Buyer buyer = new Buyer();
        buyer.setId(id);
        buyer.setName(name);
        buyer.setSurname(surname);
        buyer.setLastname(lastname);
        buyer.setAddress(address);
        buyer.setIdNumber(idNumber);
        buyer.setPassportSeries(passportSeries);
        buyer.setPassportNumber(passportNumber);
        buyer.setPassportWhoIssued(passportWhoIssued);
        buyer.setPhone(phone);
        buyer.setEmail(email);
        buyer.setNote(note);
        Realtor realtor1 = new Realtor();
        realtor1.setId(realtor);
        buyer.setRealtor(realtor1);
        User user = new User();
        user.setId(manager);
        buyer.setUser(user);
        return buyer;
    }
}
