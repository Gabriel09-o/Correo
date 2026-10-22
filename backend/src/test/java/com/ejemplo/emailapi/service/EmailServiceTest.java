package com.ejemplo.emailapi.service;

import com.ejemplo.emailapi.dto.EmailRequest;
import com.ejemplo.emailapi.exception.EmailSendException;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private final SendGrid sendGrid = mock(SendGrid.class);
    private final EmailService service = new EmailService(sendGrid, "remitente@ejemplo.com");
    private final EmailRequest request = new EmailRequest("a@ejemplo.com", "Asunto", "Mensaje");

    @Test
    void acepta202() throws IOException {
        when(sendGrid.api(any(Request.class))).thenReturn(new Response(202, "", null));
        assertDoesNotThrow(() -> service.send(request));
    }

    @Test
    void lanzaExcepcionSiSendGridRechaza() throws IOException {
        when(sendGrid.api(any(Request.class))).thenReturn(new Response(403, "", null));
        assertThrows(EmailSendException.class, () -> service.send(request));
    }

    @Test
    void lanzaExcepcionSiHayErrorDeRed() throws IOException {
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("sin red"));
        assertThrows(EmailSendException.class, () -> service.send(request));
    }
}
