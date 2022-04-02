package org.vietsearch.essme.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;


public class OnSendResponseEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String expert_email;

    @Getter @Setter
    private String customer_email;

    public OnSendResponseEvent(String expert_email, String customer_email) {
        super(customer_email);
        this.expert_email = expert_email;
        this.customer_email = customer_email;
    }


}
