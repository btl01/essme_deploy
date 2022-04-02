package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vietsearch.essme.service.IEmailService;

import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api/mail")
public class TestMailController {
    @Autowired
    private IEmailService iEmailService;

    @GetMapping
    public ResponseEntity sendMail(@RequestParam("email") @Email String email){
        iEmailService.sendAcceptRequestEmail("test@gmail.com", email);
        return new ResponseEntity("email has send to " + email, HttpStatus.OK);
    }
}
