package com.example.NewBuildingFinance.entities.cashRegister;

public enum Article { // стаття
    // приход
    apartmentPayment("Payment for an apartment"), // оплата за квартиру
    otherParish("Other parish"), // прочий приход
    // расход
    commissionAgencies("Commission agencies"), // комисионние агенства
    commissionManager("Commission manager"), // комисионние мененджери
    constructionCosts("Construction costs"), // расходы на строительство
    withdrawalMoneyCashRegister("Withdrawal money from the cash register"); // видача денег из касси

    Article(String s) {
    }
}
