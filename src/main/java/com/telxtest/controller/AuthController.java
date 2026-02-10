package com.telxtest.controller;

import com.telxtest.model.AuthRequestResponse;
import com.telxtest.model.AuthVerifyResponse;
import com.telxtest.model.OtpRequest;
import com.telxtest.model.OtpVerifyRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/request-otp")
    public AuthRequestResponse requestOtp(@Valid @RequestBody OtpRequest request) {
        return new AuthRequestResponse(UUID.randomUUID().toString(), 120);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthVerifyResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        if (!"0000".equals(request.code())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new AuthVerifyResponse(UUID.randomUUID().toString()));
    }
}
