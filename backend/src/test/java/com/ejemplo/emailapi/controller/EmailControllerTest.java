package com.ejemplo.emailapi.controller;

import com.ejemplo.emailapi.exception.EmailSendException;
import com.ejemplo.emailapi.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired MockMvc mvc;
    @MockBean EmailService emailService;

    private static final String URL = "/api/emails/send";

    @Test
    void envioValidoDevuelve202() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"a@ejemplo.com\",\"subject\":\"Hola\",\"message\":\"Texto\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void correoInvalidoDevuelve400() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"no-es-correo\",\"subject\":\"Hola\",\"message\":\"Texto\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void asuntoVacioDevuelve400() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"a@ejemplo.com\",\"subject\":\"\",\"message\":\"Texto\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonIncompletoDevuelve400() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"a@ejemplo.com\",\"message\":\"Texto\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void falloDeSendGridDevuelve502() throws Exception {
        doThrow(new EmailSendException("fallo")).when(emailService).send(any());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"a@ejemplo.com\",\"subject\":\"Hola\",\"message\":\"Texto\"}"))
                .andExpect(status().isBadGateway());
    }
}
