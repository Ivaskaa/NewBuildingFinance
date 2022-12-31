package com.example.NewBuildingFinance.others.mail.context;

import java.util.Date;

public class EmailContextBuyerPassword extends AbstractEmailContext{
    private String password;

    public EmailContextBuyerPassword() {
        setContext();
    }

    public void setPassword(String password) {
        this.password = password;
        put("password", password);
    }

    public String getPassword() {
        return password;
    }
}
