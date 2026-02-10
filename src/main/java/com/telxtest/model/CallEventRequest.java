package com.telxtest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import org.springframework.lang.Nullable;

public record CallEventRequest(
        @NotBlank String provider,
        @NotBlank String callSid,
        @NotBlank String status,
        @NotBlank String from,
        @NotBlank String to,
        @PositiveOrZero @Nullable Integer durationSec,
        @Nullable Instant timestamp
) {
}
