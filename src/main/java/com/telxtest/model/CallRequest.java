package com.telxtest.model;

import jakarta.validation.constraints.NotBlank;

public record CallRequest(
        @NotBlank String from,
        @NotBlank String to
) {
}
