package com.ejemplo.emailapi.service;

import com.ejemplo.emailapi.dto.EmailRequest;
import com.ejemplo.emailapi.exception.EmailSendException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final SendGrid sendGrid;
    private final String from;

    public EmailService(SendGrid sendGrid, @Value("${sendgrid.from}") String from) {
        this.sendGrid = sendGrid;
        this.from = from;
    }

    /** Envía el correo. Lanza EmailSendException si SendGrid no lo acepta (202). */
    public void send(EmailRequest request) {
        // El remitente sale de la configuración, nunca del cliente.
        // Se usa texto plano para evitar inyección de HTML.
        Mail mail = new Mail(new Email(from), request.subject(), new Email(request.to()),
                new Content("text/plain", request.message()));
        try {
            Request sgRequest = new Request();
            sgRequest.setMethod(Method.POST);
            sgRequest.setEndpoint("mail/send");
            sgRequest.setBody(mail.build());

            Response response = sendGrid.api(sgRequest);

            if (response.getStatusCode() != 202) {
                // Solo registramos el código, nunca la API Key.
                log.warn("SendGrid respondió con código {}", response.getStatusCode());
                throw new EmailSendException("El proveedor de correo rechazó la solicitud (código "
                        + response.getStatusCode() + "). Revisa el remitente y la API Key.");
            }
        } catch (IOException e) {
            log.error("Error de comunicación con SendGrid: {}", e.getMessage());
            throw new EmailSendException("No se pudo comunicar con el proveedor de correo");
        }
    }
}
