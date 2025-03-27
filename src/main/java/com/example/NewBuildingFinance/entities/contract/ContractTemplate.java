package com.example.NewBuildingFinance.entities.contract;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "contract_template")
public class ContractTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Must not be empty")
    @Size(max = 50, message = "Must be less 50 characters")
    private String name;
    @NotEmpty(message = "Must not be empty")
    @Size(max = 5000, message = "Must be less 5000 characters")
    private String text;
    private boolean main = false;
    private boolean deleted = false;

    @Override
    public String toString() {
        return "ContractTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", main=" + main +
                ", deleted=" + deleted +
                '}';
    }
}
