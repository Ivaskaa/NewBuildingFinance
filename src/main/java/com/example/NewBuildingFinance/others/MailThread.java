package com.example.NewBuildingFinance.others;

import com.example.NewBuildingFinance.service.mail.MailServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.mail.MessagingException;

@AllArgsConstructor
@Log4j2
public class MailThread extends Thread{
    private final MailServiceImpl mailServiceImpl;
    private final EmailContext emailContext;
    public void run() {
        try{
            mailServiceImpl.send(emailContext);
        } catch (
                MessagingException e) {
            log.warn("failed to send email");
            e.printStackTrace();
        }
    }

}
