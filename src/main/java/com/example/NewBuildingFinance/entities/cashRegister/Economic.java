package com.example.NewBuildingFinance.entities.cashRegister;

import lombok.Getter;

@Getter
public enum Economic {
    INCOME("Income"), // дохід
    SPENDING("Spending"); // витрати

    private final String value;
    Economic(String value) {
        this.value = value;
    }
}
