package com.telxtest.model;

import jakarta.validation.constraints.NotBlank;

public record Contact(
        String id,
        @NotBlank String name,
        @NotBlank String phone
) {
}
