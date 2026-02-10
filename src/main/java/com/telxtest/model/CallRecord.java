package com.telxtest.model;

import java.time.Instant;
import java.math.BigDecimal;

public record CallRecord(
        String id,
        String from,
        String to,
        Instant startedAt,
        String status,
        String provider,
        String callSid,
        Integer durationSec,
        Instant endedAt,
        BigDecimal cost
) {
}
