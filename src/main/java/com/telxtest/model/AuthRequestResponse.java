package com.telxtest.model;

public record AuthRequestResponse(
        String requestId,
        int expiresInSeconds
) {
}
