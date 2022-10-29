package com.example.NewBuildingFinance.entities.flat;

import lombok.Getter;

@Getter
public enum StatusFlat {
    ACTIVE("Active"),
    NOTACTIVE("Not active"),
    SOLD("Sold"),
    REMOVEDFROMSALE("Removed from sale"),
    RESERVE("Reserve"),
    BOOKING("Booking");

    private final String value;
    StatusFlat(String value) {
        this.value = value;
    }
}
