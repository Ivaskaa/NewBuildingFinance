package com.example.NewBuildingFinance.service.mail;

import com.example.NewBuildingFinance.others.AbstractEmailContext;

import javax.mail.MessagingException;

public interface MailService {

    void send(AbstractEmailContext email) throws MessagingException;
}
