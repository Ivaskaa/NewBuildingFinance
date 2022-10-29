package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CurrencyDto {
    private Long id;
    @NotEmpty(message = "Must not be empty")
    private String cashRegister;
    @NotEmpty(message = "Must not be empty")
    private String name;
    @NotEmpty(message = "Must not be empty")
    private String price;

    public InternalCurrency build(){
        InternalCurrency currency = new InternalCurrency();
        currency.setId(id);
        currency.setCashRegister(cashRegister);
        currency.setName(name);
        currency.setPrice(Double.valueOf(price));
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
