package com.bmsedge.asset.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationListener.class);
    private final EmailNotificationService notificationService;

    public EmailNotificationListener(EmailNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmailNotification(EmailNotificationEvent event) {
        try {
            notificationService.send(event);
        } catch (RuntimeException e) {
            logger.error("Email notification failed for recipient {}", event.recipientEmail(), e);
        }
    }
}