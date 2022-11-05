package com.example.NewBuildingFinance.entities.cashRegister;

import lombok.Getter;

@Getter
public enum Article { // стаття
    // приход
    FLAT_PAYMENT("Flat payment"), // оплата за квартиру
    OTHER_PAYMENT("Other payment"), // прочий приход
    // расход
    COMMISSION_AGENCIES("Commission agencies"), // комисионние агенства
    COMMISSION_MANAGER("Commission manager"), // комисионние мененджери
    CONSTRUCTION_COSTS("Construction costs"), // расходы на строительство
    MONEY_FOR_DIRECTOR("Money for director"); // видача денег из касси директору

    private final String value;
    Article(String value) {
        this.value = value;
    }
}
