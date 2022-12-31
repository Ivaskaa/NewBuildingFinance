package com.example.NewBuildingFinance.dto;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CurrencyDto {
    private Long id;
    @NotBlank(message = "Must not be empty")
    @Pattern(regexp = "^((?![\\s]).)*$", message = "Must not contain spaces")
    private String cashRegister;
    @NotBlank(message = "Must not be empty")
    private String name;
    @NotNull(message = "Must not be empty")
    @Digits(integer=3, fraction=4, message = "Must be in ###.#### format")
    @DecimalMin(value = "0.0001", message = "Must be greater then 0.0001")
    @DecimalMax(value = "1000.0", message = "Must be less then 1000")
    private BigDecimal price;

    public InternalCurrency build(){
        InternalCurrency currency = new InternalCurrency();
        currency.setId(id);
        currency.setCashRegister(cashRegister);
        currency.setName(name);
        currency.setPrice(price.doubleValue());
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
