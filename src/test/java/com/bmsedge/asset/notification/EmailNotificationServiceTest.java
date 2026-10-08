package com.bmsedge.asset.notification;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmailNotificationServiceTest {

    @Test
        void rendersTemplateAndSendsPlainTextEmail() throws Exception {
        JavaMailSender sender = mock(JavaMailSender.class);
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(message);
        EmailNotificationService service = new EmailNotificationService(
                sender, true, "assets@example.com");
        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setAssetName("<Laptop & Dock>");
        asset.setSerialNumber("SN-100");
        asset.setStatus(AssetStatus.IN_USE);
        EmailNotificationEvent event = EmailNotificationEvent.assetAssigned(
                "employee@example.com", "Alex", asset, "https://assets.example.com");

        service.send(event);

        verify(sender).send(message);
        assertEquals("Asset assigned: <Laptop & Dock>", message.getSubject());
        assertEquals("employee@example.com", message.getAllRecipients()[0].toString());
        assertEquals("assets@example.com", message.getFrom()[0].toString());
        assertTrue(message.isMimeType("text/plain"));
        String text = (String) message.getContent();
        assertTrue(text.contains("An asset has been assigned to Alex."));
        assertTrue(text.contains("Asset Name: <Laptop & Dock>"));
        assertTrue(text.contains("Serial Number: SN-100"));
        assertTrue(text.contains("https://assets.example.com/assets/asset-1"));
    }

    @Test
    void doesNotSendWhenNotificationsAreDisabled() {
        JavaMailSender sender = mock(JavaMailSender.class);
        EmailNotificationService service = new EmailNotificationService(
                sender, false, "assets@example.com");
        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setAssetName("Laptop");
        asset.setStatus(AssetStatus.IN_USE);

        service.send(EmailNotificationEvent.assetAssigned(
                "employee@example.com", "Alex", asset, "https://assets.example.com"));

        verifyNoInteractions(sender);
    }
}