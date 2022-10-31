package com.example.NewBuildingFinance.entities.contract;

import com.example.NewBuildingFinance.dto.contract.ContractTableDto;
import com.example.NewBuildingFinance.dto.contract.ContractTableDtoForAgency;
import com.example.NewBuildingFinance.dto.contract.ContractTableDtoForBuyers;
import com.example.NewBuildingFinance.dto.contract.ContractUploadDto;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "buyer_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Buyer buyer;
    @JoinColumn(name = "template_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private ContractTemplate contractTemplate;
    @JoinColumn(name = "flat_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonIgnore
    private Flat flat;
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private String name;
    private String surname;
    private String lastname;
    private String buyerAddress;
    private Long idNumber;
    private String passportSeries;
    private Integer passportNumber;
    private Integer passportWhoIssued;
    private String phone;
    private String email;

    private Integer flatNumber;
    private Double flatArea;
    private Integer flatFloor;
    private String flatAddress;
    private Integer price;

    private Date date;
    private String comment;

    private String object;

    private boolean deleted = false;

    public ContractUploadDto buildUploadDto(){
        ContractUploadDto contract = new ContractUploadDto();
        contract.setId(id);
        contract.setBuyer(buyer);
        contract.setContractTemplate(contractTemplate);

        contract.setStatus(status);

        contract.setName(name);
        contract.setSurname(surname);
        contract.setLastname(lastname);
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
        contract.setDate(date);
        contract.setComment(comment);
        return contract;
    }

    public ContractTableDto buildTableDto(){
        ContractTableDto contract = new ContractTableDto();
        contract.setId(id);
        contract.setBuyer(
                buyer.getSurname() + " " +
                buyer.getName() + " " +
                buyer.getLastname());
        contract.setFlatNumber(flatNumber);
        contract.setObject(object);
        contract.setDate(date);
        contract.setComment(comment);
        return contract;
    }

    public ContractTableDtoForBuyers buildTableDtoForBuyers(){
        ContractTableDtoForBuyers contract = new ContractTableDtoForBuyers();
        contract.setId(id);

        contract.setFlatId(flat.getId());
        contract.setFlatNumber(flatNumber);
        contract.setFlatArea(flatArea);
        contract.setStatus(status.getValue());
        contract.setPrice(price);
        contract.setObject(object);
        contract.setDate(date);
        contract.setAgency(buyer.getRealtor().getAgency().getName());
        contract.setRealtor(buyer.getRealtor().getSurname() + " " + buyer.getRealtor().getName());
        return contract;
    }

    public ContractTableDtoForAgency buildTableDtoForAgency(){
        ContractTableDtoForAgency contract = new ContractTableDtoForAgency();
        contract.setId(id);

        contract.setFlatId(flat.getId());
        contract.setFlatNumber(flatNumber);
        contract.setFlatArea(flatArea);
        contract.setStatus(status.getValue());
        contract.setPrice(price);
        contract.setObject(object);
        contract.setDate(date);
        contract.setBuyer(buyer.getSurname() + " " + buyer.getName() + " " + buyer.getLastname());
        return contract;
    }
}
