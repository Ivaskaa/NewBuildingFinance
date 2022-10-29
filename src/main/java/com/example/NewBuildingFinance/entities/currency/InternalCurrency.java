package com.example.NewBuildingFinance.entities.currency;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "internal_currency")
public class InternalCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String cashRegister;
    @NotEmpty(message = "Must not be empty")
    private String name;
    @NotNull(message = "Must not be empty")
    private Double price;

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", cashRegister='" + cashRegister + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
