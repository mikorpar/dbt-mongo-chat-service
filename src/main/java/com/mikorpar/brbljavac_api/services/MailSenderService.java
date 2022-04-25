package com.mikorpar.brbljavac_api.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String hostAddress;
    @Value("${spring.mail.username}")
    private String sender;
    @Value("${server.port}")
    private int hostPort;
    @Value("${registration.verification.endpoint}")
    private String verEndpoint;

    private void sendMail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Mail sent to {} successfully", to);
        } catch (MessagingException e) {
            log.error("Error send error: ", e);
        }
    }

    @Async
    public void sendActivationMail(String toEmail, String username, String token) {
        final String subject = "Brbrljavac registration validation";
        final String url = String.format("http://%s:%d/%s?token=%s", hostAddress, hostPort, verEndpoint, token);

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("ver_link", url);
        String body = templateEngine.process("verification_email.htm", context);

        sendMail(toEmail, subject, body);
    }

    @Async
    public void sendPasswdRecoveryMail(String toEmail, String username, String password) {
        final String subject = "Brbrljavac password recovery";

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("password", password);

        String body = templateEngine.process("passw_recovery.htm", context);
        sendMail(toEmail, subject, body);
    }
}
