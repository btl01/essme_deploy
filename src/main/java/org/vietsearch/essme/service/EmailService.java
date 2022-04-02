package org.vietsearch.essme.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EmailService implements IEmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendAcceptRequestEmail(String expertEmail, String recipientEmail) {
        String content = "Your request has a new response from " + expertEmail;
        String subject = "REQUEST HAS A NEW RESPONSE";
        sendEmailTo(recipientEmail, subject , content);
    }

    private void sendEmailTo(String recipientEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
