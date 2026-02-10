package com.telxtest.model;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record TopUpRequest(
        @DecimalMin("0.01") BigDecimal amount
) {
}
