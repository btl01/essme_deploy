package org.vietsearch.essme.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.vietsearch.essme.service.IEmailService;

public class SendResponseEventListener implements ApplicationListener<OnSendResponseEvent> {
    @Autowired
    private IEmailService emailService;

    /*
     * handle event and send accept email
     */
    @Override
    public void onApplicationEvent(OnSendResponseEvent event) {
        sendResponseEmail(event.getExpert_email(), event.getCustomer_email());
    }

    private void sendResponseEmail(String expert_email, String customer_email) {
        emailService.sendAcceptRequestEmail(expert_email, customer_email);
    }
}
