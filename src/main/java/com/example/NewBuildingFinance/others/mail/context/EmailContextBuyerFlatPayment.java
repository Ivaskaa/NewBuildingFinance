package com.example.NewBuildingFinance.others.mail.context;

import java.util.Date;

public class EmailContextBuyerFlatPayment extends AbstractEmailContext {
    private Date todayDate;
    private Date date;
    private Double planned;
    private Double actually;

    public EmailContextBuyerFlatPayment() {
        setContext();
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
        put("todayDate", todayDate);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        put("date", date);
    }

    public Double getPlanned() {
        return planned;
    }

    public void setPlanned(Double planned) {
        this.planned = planned;
        put("planned", planned);
    }

    public Double getActually() {
        return actually;
    }

    public void setActually(Double actually) {
        this.actually = actually;
        put("actually", actually);
    }
}
