package com.example.NewBuildingFinance.entities.contract;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "contract_templates")
public class ContractTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Must not be empty")
    @Size(max = 50, message = "Must be less 50 characters")
    private String name;
    @NotEmpty(message = "Must not be empty")
    @Size(max = 50000, message = "Must be less 50000 characters")
    private String text;
    private boolean main = false;
    private boolean deleted = false;
}
