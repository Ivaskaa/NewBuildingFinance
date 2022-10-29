package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.others.AbstractEmailContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

@Service
@Log4j2
@AllArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

//    @Value("${spring.mail.username}")
//    private String sender;

    public void send(AbstractEmailContext email) throws MessagingException {
        log.info("send email to: {}", email.getTo());
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());  // так само як model передає насторінку
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);

        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);
        log.info("success send email to: {}", email.getTo());
        emailSender.send(message);
    }
}

//@Service
//@Log4j2
//public class MailService {
//    @Autowired
//    private JavaMailSender emailSender;
//
//    @Value("${spring.mail.username}")
//    private String sender;
//
//    public void send(String emailTo, String subject, String message){
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//
//        mailMessage.setFrom(sender);
//        mailMessage.setTo(emailTo);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(message);
//        emailSender.send(mailMessage);
//    }
//
//    public String subjectValidation(String subject) {
//        if(subject.equals("")){
//            return "Subject shouldn't be empty";
//        }
//        if(subject.length() > 255){
//            return "Subject must be less than 255 characters";
//        }
//        return null;
//    }
//
//    public String messageValidation(String message) {
//        if(message.equals("")){
//            return "Message shouldn't be empty";
//        }
//        if(message.length() > 255){
//            return "Message must be less than 255 characters";
//        }
//        return null;
//    }
//}

