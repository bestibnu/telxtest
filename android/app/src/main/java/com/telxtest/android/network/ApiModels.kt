package com.telxtest.android.network

data class OtpRequest(val phone: String)
data class OtpVerifyRequest(val phone: String, val code: String)
data class AuthRequestResponse(val requestId: String, val expiresInSeconds: Int)
data class AuthVerifyResponse(val token: String)

data class CallRequest(val from: String, val to: String)
data class CallRecord(
    val id: String,
    val from: String,
    val to: String,
    val startedAt: String,
    val status: String
)

data class CreditBalance(val balance: String, val currency: String)
data class TopUpRequest(val amount: String)
