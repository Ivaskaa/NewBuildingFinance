package com.example.NewBuildingFinance.dto.contract;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.entities.flat.Flat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
public class ContractUploadDto {
    private Long id;
    private Flat flat;
    private Object object;
    private Buyer buyer;
    private ContractTemplate contractTemplate;

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
}
