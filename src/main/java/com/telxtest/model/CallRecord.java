package com.telxtest.model;

import java.time.Instant;

public record CallRecord(
        String id,
        String from,
        String to,
        Instant startedAt,
        String status
) {
}
