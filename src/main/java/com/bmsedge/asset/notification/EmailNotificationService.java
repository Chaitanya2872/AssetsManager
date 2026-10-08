package com.bmsedge.asset.notification;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final String TEMPLATE_DIRECTORY = "templates/email/";

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String fromAddress;

    public EmailNotificationService(
            JavaMailSender mailSender,
            @Value("${app.notifications.email.enabled:false}") boolean enabled,
            @Value("${app.notifications.email.from:no-reply@localhost}") String fromAddress) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
    }

    public void send(EmailNotificationEvent event) {
        if (!enabled) {
            logger.debug("Email notifications are disabled; skipped email to {}", event.recipientEmail());
            return;
        }

        if (!isValidEmail(event.recipientEmail())) {
            logger.info("Skipped notification because the recipient email is missing or invalid");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(event.recipientEmail());
            helper.setSubject(event.templateValues().get("subject"));
            helper.setText(renderTemplate(event), false);
            mailSender.send(message);
            logger.info("Sent asset notification email to {}", event.recipientEmail());
        } catch (Exception e) {
            throw new IllegalStateException("Could not send asset notification email", e);
        }
    }

    private String renderTemplate(EmailNotificationEvent event) throws IOException {
        Resource template = new ClassPathResource(TEMPLATE_DIRECTORY + event.templateName());
        String text = StreamUtils.copyToString(template.getInputStream(), StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : event.templateValues().entrySet()) {
            text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return text;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        try {
            InternetAddress address = new InternetAddress(email, true);
            address.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}