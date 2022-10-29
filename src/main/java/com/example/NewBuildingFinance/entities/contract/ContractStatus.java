package com.example.NewBuildingFinance.entities.contract;

import lombok.Getter;

@Getter
public enum ContractStatus {
    SIGNED("Signed"),
    ONREVIEW("On reviev");

    private final String value;
    ContractStatus(String value) {
        this.value = value;
    }
}
