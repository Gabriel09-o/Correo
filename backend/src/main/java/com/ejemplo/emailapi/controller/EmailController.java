package com.ejemplo.emailapi.controller;

import com.ejemplo.emailapi.dto.ApiResponse;
import com.ejemplo.emailapi.dto.EmailRequest;
import com.ejemplo.emailapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> send(@Valid @RequestBody EmailRequest request) {
        emailService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("success", "Solicitud de envío aceptada"));
    }
}
