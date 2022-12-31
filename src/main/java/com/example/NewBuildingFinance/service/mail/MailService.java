package com.example.NewBuildingFinance.service.mail;

import com.example.NewBuildingFinance.others.mail.context.AbstractEmailContext;

import javax.mail.MessagingException;

public interface MailService {

    /**
     * settings before updating
     * @param email email context
     * @throws MessagingException
     */
    void send(AbstractEmailContext email) throws MessagingException;
}
