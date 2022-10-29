package com.example.NewBuildingFinance.entities.object;


import lombok.Getter;

@Getter
public enum StatusObject {
    ONSALE("On sale"), // в продіжі
    SOLD("Sold"); // продано

    private final String value;
    StatusObject(String value) {
        this.value = value;
    }
}
