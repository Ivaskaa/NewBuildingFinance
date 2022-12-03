package com.example.NewBuildingFinance.entities.buyer;

import lombok.Getter;

@Getter
public enum DocumentStyle {
    ID_CARD("Id card"),
    PASSPORT("Passport");

    private final String value;
    DocumentStyle(String value) {
        this.value = value;
    }
}
