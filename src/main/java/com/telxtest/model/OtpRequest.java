package com.telxtest.model;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank String phone
) {
}
