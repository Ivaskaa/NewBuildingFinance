package com.example.NewBuildingFinance.entities.cashRegister;

import lombok.Data;
import lombok.Getter;

@Getter
public enum StatusCashRegister {
    COMPLETED("Completed"),
    PLANNED("Planned");

    private final String value;
    StatusCashRegister(String value) {
        this.value = value;
    }
}
