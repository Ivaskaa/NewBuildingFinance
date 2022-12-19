package com.example.NewBuildingFinance.others.mail;

import com.example.NewBuildingFinance.others.mail.context.AbstractEmailContext;
import com.example.NewBuildingFinance.service.mail.MailServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.mail.MessagingException;

@AllArgsConstructor
@Log4j2
public class MailThread extends Thread{
    private final MailServiceImpl mailServiceImpl;
    private final AbstractEmailContext abstractEmailContext;

    public void run() {
        try{
            mailServiceImpl.send(abstractEmailContext);
        } catch (MessagingException e) {
            log.warn("failed to send email");
            e.printStackTrace();
        }
    }
}
