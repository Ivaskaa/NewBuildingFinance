package com.example.NewBuildingFinance.dto.contract;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
public class ContractSaveDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long flatId;
    @NotNull(message = "Must not be empty")
    private Long objectId;
    @NotNull(message = "Must not be empty")
    private Long buyerId;
    @NotNull(message = "Must not be empty")
    private Long contractTemplateId;
    @NotNull(message = "Must not be empty")
    private ContractStatus status;
    @NotEmpty(message = "Must not be empty")
    private String name;
    @NotEmpty(message = "Must not be empty")
    private String surname;
    @NotEmpty(message = "Must not be empty")
    private String lastname;
    @NotEmpty(message = "Must not be empty")
    private String buyerAddress;
    @NotNull(message = "Must not be empty")
    private Long idNumber;
    @NotEmpty(message = "Must not be empty")
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
    @NotNull(message = "Must not be empty")
    private Integer flatNumber;
    @NotNull(message = "Must not be empty")
    private Double flatArea;
    @NotNull(message = "Must not be empty")
    private Integer flatFloor;
    @NotEmpty(message = "Must not be empty")
    private String flatAddress;
    @NotNull(message = "Must not be empty")
    private Integer price;
    @NotEmpty(message = "Must not be empty")
    private String date;
    private String comment;

    public Contract build() throws ParseException {
        Contract contract = new Contract();

        contract.setId(id);

        ContractTemplate contractTemplate = new ContractTemplate();
        contractTemplate.setId(contractTemplateId);
        contract.setContractTemplate(contractTemplate);

        Buyer buyer = new Buyer();
        buyer.setId(buyerId);
        contract.setBuyer(buyer);

        if(date!= null && !date.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("yyyy-MM-dd");
            contract.setDate(format.parse(date));
        }

        contract.setStatus(status);

        contract.setName(name);
        contract.setLastname(lastname);
        contract.setSurname(surname);
        contract.setBuyerAddress(buyerAddress);
        contract.setIdNumber(idNumber);
        contract.setPassportSeries(passportSeries);
        contract.setPassportNumber(passportNumber);
        contract.setPassportWhoIssued(passportWhoIssued);
        contract.setPhone(phone);
        contract.setEmail(email);

        contract.setFlatNumber(flatNumber);
        contract.setFlatArea(flatArea);
        contract.setFlatFloor(flatFloor);
        contract.setFlatAddress(flatAddress);
        contract.setPrice(price);

        contract.setComment(comment);
        return contract;
    }
}
