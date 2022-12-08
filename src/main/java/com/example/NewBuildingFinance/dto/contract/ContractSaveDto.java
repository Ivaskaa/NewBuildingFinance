package com.example.NewBuildingFinance.dto.contract;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.buyer.DocumentStyle;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.entities.flat.Flat;
import lombok.Data;

import javax.validation.constraints.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String buyerAddress;
    @NotNull(message = "Must not be empty")
    private Long idNumber;

    @NotEmpty(message = "You must choose something")
    private String documentStyle;

    private String passportSeries;
    private Integer passportNumber;
    private String passportWhoIssued;

    private Long idCardNumber;
    private Integer idCardWhoIssued;

    @NotBlank(message = "Must not be empty")
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
    @NotBlank(message = "Must not be empty")
    private String flatAddress;
    @NotNull(message = "Must not be empty")
    private Integer price;
    @NotNull(message = "Must not be empty")
    private Date date;
    private String comment;

    public Contract build() {
        Contract contract = new Contract();

        contract.setId(id);

        ContractTemplate contractTemplate = new ContractTemplate();
        contractTemplate.setId(contractTemplateId);
        contract.setContractTemplate(contractTemplate);

        Buyer buyer = new Buyer();
        buyer.setId(buyerId);
        contract.setBuyer(buyer);

        Flat flat = new Flat();
        flat.setId(flatId);
        contract.setFlat(flat);

        contract.setDate(date);
        contract.setStatus(status);

        contract.setName(name);
        contract.setLastname(lastname);
        contract.setSurname(surname);
        contract.setBuyerAddress(buyerAddress);
        contract.setIdNumber(idNumber);

        if(documentStyle != null && !documentStyle.equals("")){
            DocumentStyle style = DocumentStyle.valueOf(documentStyle);
            contract.setDocumentStyle(style);
            if(style.equals(DocumentStyle.ID_CARD)){
                contract.setIdCardNumber(idCardNumber);
                contract.setIdCardWhoIssued(idCardWhoIssued);
            } else if (style.equals(DocumentStyle.PASSPORT)) {
                contract.setPassportNumber(passportNumber);
                contract.setPassportSeries(passportSeries);
                contract.setPassportWhoIssued(passportWhoIssued);
            }
        }
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
