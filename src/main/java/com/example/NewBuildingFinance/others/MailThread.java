package com.example.NewBuildingFinance.others;

import com.example.NewBuildingFinance.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.mail.MessagingException;

@AllArgsConstructor
@Log4j2
public class MailThread extends Thread{
    private final MailService mailService;
    private final EmailContext emailContext;
    public void run() {
        try{
            mailService.send(emailContext);
        } catch (
                MessagingException e) {
            log.warn("failed to send email");
            e.printStackTrace();
        }
    }

}
