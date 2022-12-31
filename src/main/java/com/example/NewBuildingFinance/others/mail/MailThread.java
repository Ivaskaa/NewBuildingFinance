package com.example.NewBuildingFinance.others.mail;

import com.example.NewBuildingFinance.others.mail.context.AbstractEmailContext;
import com.example.NewBuildingFinance.service.mail.MailService;
import com.example.NewBuildingFinance.service.mail.MailServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.mail.MessagingException;

@AllArgsConstructor
@Log4j2
public class MailThread extends Thread{
    private final MailService mailService;
    private final AbstractEmailContext abstractEmailContext;

    public void run() {
        try{
            mailService.send(abstractEmailContext);
        } catch (MessagingException e) {
            log.warn("failed to send email");
            e.printStackTrace();
        }
    }
}
