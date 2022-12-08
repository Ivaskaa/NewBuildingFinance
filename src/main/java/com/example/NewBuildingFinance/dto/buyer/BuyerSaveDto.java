package com.example.NewBuildingFinance.dto.buyer;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.buyer.DocumentStyle;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class BuyerSaveDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String name;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String surname;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String lastname;
    @NotBlank(message = "Must not be empty")
    private String address;
    @NotNull(message = "Must not be empty")
    private Long idNumber;

    @NotEmpty(message = "You must choose something")
    private String documentStyle;

    private String passportSeries;
    private Integer passportNumber;
    private String passportWhoIssued;

    private Long idCardNumber;
    private Integer idCardWhoIssued;

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
        buyer.setDocumentStyle(DocumentStyle.valueOf(documentStyle));
        if(buyer.getDocumentStyle().equals(DocumentStyle.ID_CARD)){
            buyer.setIdCardNumber(idCardNumber);
            buyer.setIdCardWhoIssued(idCardWhoIssued);
        } else if (buyer.getDocumentStyle().equals(DocumentStyle.PASSPORT)){
            buyer.setPassportSeries(passportSeries);
            buyer.setPassportNumber(passportNumber);
            buyer.setPassportWhoIssued(passportWhoIssued);
        }
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
