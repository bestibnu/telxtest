package com.telxtest.model;

import jakarta.validation.constraints.NotBlank;

public record OtpVerifyRequest(
        @NotBlank String phone,
        @NotBlank String code
) {
}
