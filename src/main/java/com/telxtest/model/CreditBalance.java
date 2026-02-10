package com.telxtest.model;

import java.math.BigDecimal;

public record CreditBalance(
        BigDecimal balance,
        String currency
) {
}
