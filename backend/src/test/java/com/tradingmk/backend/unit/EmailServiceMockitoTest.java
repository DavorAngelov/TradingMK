package com.tradingmk.backend.unit;

import com.tradingmk.backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceMockitoTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_buildsMessageWithCorrectFieldsAndSends() {
        emailService.sendEmail("trader@test.com", "Trade Approved", "Your BUY order was approved.");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertEquals("tradingmkalerts@gmail.com", sent.getFrom());
        assertEquals("trader@test.com", sent.getTo()[0]);
        assertEquals("Trade Approved", sent.getSubject());
        assertEquals("Your BUY order was approved.", sent.getText());
    }
}