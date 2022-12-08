package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
public class CurrencyDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String cashRegister;
    @NotBlank(message = "Must not be empty")
    private String name;
    @NotNull(message = "Must not be empty")
    @Range(min = 0, max = 1000)
    private Double price;

    public InternalCurrency build(){
        InternalCurrency currency = new InternalCurrency();
        currency.setId(id);
        currency.setCashRegister(cashRegister);
        currency.setName(name);
        currency.setPrice(price);
        return currency;
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
                "id=" + id +
                ", cashRegister='" + cashRegister + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
